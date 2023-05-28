package com.example.whiteboard;

import android.app.Activity;
import android.content.Context;
import android.content.Entity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.debugger.ConsoleDebugger;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SmackXMPP {

    private XMPPTCPConnectionConfiguration configuration;
    private AbstractXMPPConnection connection;
    PubSubManager pubSubManager;
    ServiceDiscoveryManager serviceDiscoveryManager;
    LeafNode leafNode = null;

    private String username;
    private String password;
    private Context mContext;
    private Handler mHandler;
    private int count = 0;
    ThreadPoolExecutor executorService;

    public SmackXMPP(Context context, String username, String password, String domain, String host) {
        executorService = new ThreadPoolExecutor(1, 5,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        SmackConfiguration.DEBUG = true;

        mContext = context;
        try {
            InetAddress addr = InetAddress.getByName(host);
            configuration = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(username, password)
                    .setXmppDomain(domain)
                    .setHostAddress(addr)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setDebuggerFactory(ConsoleDebugger.Factory.INSTANCE)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = new XMPPTCPConnection(configuration);
    }

    public void connect() {

        executorService.execute(() -> {
            try {
                connection.connect().login();
                if (connection.isAuthenticated())
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
                List<String> features = serviceDiscoveryManager.getFeatures();
                pubSubManager = PubSubManager.getInstance(connection);
                DiscoverInfo supportedFeatures = pubSubManager.getSupportedFeatures();
                ConfigureForm defaultConfiguration = pubSubManager.getDefaultConfiguration();
                leafNode = pubSubManager.getLeafNode("noderealtime");
                //ConfigureForm configureForm = leafNode.getNodeConfiguration();
                leafNode.addItemEventListener(new ItemEventListener() {
                    @Override
                    public void handlePublishedItems(ItemPublishEvent items) {
                        PayloadItem payloadItem = (PayloadItem) items.getItems().get(0);
                        if (payloadItem.getId().equals(MainActivity.currentUser))
                            return;
                        Message message = mHandler.obtainMessage();
                        message.obj = payloadItem;
                        message.sendToTarget();
                    }
                });
                //createNode();
                if(MainActivity.subscribe)
                    subscribe();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void createNode() {
        try {
            pubSubManager.createNode("noderealtime");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(final PayloadItem payloadItem) {
        Log.d("SmackXMPP", "Publishing payload");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    leafNode.publish(payloadItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadPreviousMessages() {
        PubSubManager pubSubManager = PubSubManager.getInstance(connection);
        LeafNode leafNode = null;
        try {
            leafNode = pubSubManager.getLeafNode("noderealtime");
            List<PayloadItem> payloadItemList = leafNode.getItems();
            List<String> previousMessages = new ArrayList<>();
            for (PayloadItem payloadItem : payloadItemList) {
                String message = ((DataForm) payloadItem.getPayload()).getField("chatMessage").getFirstValue();
                previousMessages.add(message);
            }
            Message message = mHandler.obtainMessage();
            message.obj = previousMessages;
            message.sendToTarget();

            int x = 3;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void subscribe() {
        try {
            leafNode.subscribe(MainActivity.currentUser + "@autojetfx.home");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void unsubscribe() {
        PubSubManager pubSubManager = PubSubManager.getInstance(connection);
        LeafNode leafNode = null;
        try {
            leafNode = pubSubManager.getLeafNode("noderealtime");
            leafNode.unsubscribe("ahmedjamil112233@xmpp.jp");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void disconnect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        });
        thread.start();
    }
}

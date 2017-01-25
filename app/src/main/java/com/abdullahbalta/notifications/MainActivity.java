package com.abdullahbalta.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int numMessages = 0;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;

    private AppCompatEditText edtNotId;

    // intent'in action'una verilecek string
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    private static final String GROUP_KEY = "group_key";

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    private List<String> senderAndMessages = new ArrayList<>();


    // Sets an ID for the notification, so it can be updated
    int notifyID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBuilder = new NotificationCompat.Builder(this);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        edtNotId = (AppCompatEditText)findViewById(R.id.edt_not_id);
    }

    public void sendSimpleNotification(View view) {

        String sender = randomString(5);
        String message = randomString(10);

        mBuilder.setSmallIcon(R.drawable.ic_insert_comment_black_48dp);
        mBuilder.setContentTitle("Basit Bildirim");
        mBuilder.setAutoCancel(true);
        mBuilder.setGroup(GROUP_KEY);

        senderAndMessages.add(sender + " " + message);

        if(numMessages == 0) {
            mBuilder.setContentText("Bu basit bir bildirimdir.");
        }else{
            mBuilder.setContentText("Bu basit güncel bir bildirimdir. " + message).setNumber(numMessages);
        }
        numMessages++;

        //Uygulama da açılacak Activity, intent olarak tanımlanıyor.
        Intent resultIntent = new Intent(this, ResultActivity.class);

        //stackBuilder nesnesi activityler arasında geri geçişi oluşturuyor.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //Açılacak activity'i parent stack'e ekliyoruz böylece, ResultActivity'den geri geldiğimiz de MainActivity açılacak.
        stackBuilder.addParentStack(ResultActivity.class);
        //Bildirim ile açılacak activity'i ekliyoruz.
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(++notifyID, mBuilder.build());
    }

    public void sendExpandedNotification(View view){
        mBuilder.setSmallIcon(R.drawable.ic_dns_black_48dp);
        mBuilder.setContentTitle("Genişleyebilir Bildirim");
        mBuilder.setContentText("Bu bir genişleyebilir bildirimdir.");

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = {"abdullah", "balta", ".", "com", "bildirimler"};
        inboxStyle.setBigContentTitle("Genişleyebilir bildirim başlıkları.");

        //Genişlenebilir alana textler ekliyoruz.
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void sendReplyNotification(View view){

        Intent intent = new Intent(this, NotificationReplyActivity.class);
        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("Yanıtla")
                .build();

        // Oluşturulan remote input nesnesini notification'a reply action olarak ekliyoruz.
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_48dp, "Yanıtla", replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        // Notification'ı oluşturup, action ekliyoruz.
                Notification newMessageNotification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_insert_comment_black_48dp)
                        .setContentTitle("Yeni mesaj")
                        .setContentText("Bir adet mesajınız var.")
                        .addAction(action)
                        .build();

        mNotificationManager.notify(5, newMessageNotification);
    }

    public void clearNotification(View view){
        mNotificationManager.cancel(Integer.parseInt(edtNotId.getText().toString()));
    }

    public void clearAllNotification(View view) {
        mNotificationManager.cancelAll();
    }

    public void summaryAllNotification(View view){
        mNotificationManager.cancelAll();

        mBuilder.setSmallIcon(R.drawable.ic_insert_comment_black_48dp);
        mBuilder.setContentTitle("Basit Bildirim");
        mBuilder.setAutoCancel(true);
        mBuilder.setGroup(GROUP_KEY);
        mBuilder.setGroupSummary(true);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_insert_comment_black_48dp));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle((numMessages + 1) + " yeni mesaj");
        inboxStyle.setSummaryText("abdullahbalta.com");

        for(int i = 0; i < senderAndMessages.size(); i++){
            inboxStyle.addLine(senderAndMessages.get(i));
        }

        mBuilder.setStyle(inboxStyle);

        mNotificationManager.notify(++notifyID, mBuilder.build());
    }

    private String randomString(int len){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public void determinateNotification(View view){
        mBuilder.setContentTitle("Mp3 İndirme");
        mBuilder.setContentText("Mp3 indiriliyor..");
        mBuilder.setSmallIcon(R.drawable.ic_file_download_black_48dp);

        // Thread başlatıyoruz.
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // 10 kere progress barı notify ediyoruz.
                        for (incr = 0; incr <= 100; incr+=5) {
                            //mBuilder.setProgress(100, incr, false);
                            mBuilder.setProgress(0, 0, true);
                            mNotificationManager.notify(0, mBuilder.build());
                            try {
                                // 2 saniye bekle
                                Thread.sleep(2*1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // Fake indirme tamamlanınca bildirimi güncelle
                        mBuilder.setContentText("İndirme tamamlandı.")
                                .setProgress(0,0,false);
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                }
        ).start();
    }

    public void headsUpNotification(View view){
        mBuilder.setContentTitle("Dikkat Et");
        mBuilder.setContentText("Bu bir heads up bildirimdir.");
        mBuilder.setSmallIcon(R.drawable.ic_thumb_up_black_48dp);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void lockScreenNotification(View view){
        Intent intent = new Intent(this, NotificationReplyActivity.class);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setSmallIcon(R.drawable.ic_music_video_black_48dp);
        mBuilder.addAction(R.drawable.ic_skip_previous_black_48dp, "Previous", replyPendingIntent);
        mBuilder.addAction(R.drawable.ic_play_arrow_black_48dp, "Play", replyPendingIntent);
        mBuilder.addAction(R.drawable.ic_skip_next_black_48dp, "Next", replyPendingIntent);
        mBuilder.setContentTitle("Wonderful music");
        mBuilder.setContentText("My Awesome Band");
        mNotificationManager.notify(0, mBuilder.build());
    }
}

package training_activity1.com.callerapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public class IncomingCallReceiver extends BroadcastReceiver {
    String incomingNumber = "";

    AudioManager audioManager;
    TelephonyManager telephonyManager;
    String name = null;
    String email=null;

    public void onReceive(Context context, Intent intent) {
        // Get AudioManager
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Get TelephonyManager
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Get incoming number
                incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String name = getName(context,incomingNumber);
                String email = getEmailId(context,incomingNumber);

                startApp(context, incomingNumber, name, email);
            }
        }
    }



      /*  if (!incomingNumber.equals("")) {
            // Get an instance of ContentResolver
            ContentResolver cr = context.getContentResolver();
            // Fetch the matching number
            //  Uri uri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL, Uri.encode("PhoneNo"));
            Cursor numbers = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[]
                            {ContactsContract.Contacts._ID, ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?", new String[]{incomingNumber}, null);

            if (numbers.getCount() <= 0) { // The incoming number is not  found in the contacts list
                startApp(context, incomingNumber, "UnKnown Number");
            } else if (numbers.getCount() > 0) {
                startApp(context, incomingNumber, name);
            }*/

    public  String getName(Context context, String number){

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
        String name = "UnknownNumber";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri,new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        try{
            if(contactLookup != null && contactLookup.getCount()>0 ){
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
        }
        finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    public  String getEmailId(Context context, String number){

        String emaildata = "";

        try {
            ContentResolver cr = context
                    .getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
            Cursor cur = cr.query(uri,
                    null,
                    null,
                    null,
                    null);
           /* Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);*/

            if (cur.getCount() > 0) {

                while (cur
                        .moveToNext()) {

                    String contactId = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts._ID));

                    // Create query to use CommonDataKinds classes to fetch emails
                    Cursor emails = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                    + " = " + contactId, null, null);

                            /*
                            //You can use all columns defined for ContactsContract.Data
                            // Query to get phone numbers by directly call data table column

                            Cursor c = getContentResolver().query(Data.CONTENT_URI,
                                      new String[] {Data._ID, Phone.NUMBER, Phone.TYPE, Phone.LABEL},
                                      Data.CONTACT_ID + "=?" + " AND "
                                              + Data.MIMETYPE + "= + Phone.CONTENT_ITEM_TYPE + ",
                                      new String[] {String.valueOf(contactId)}, null);
                            */

                    while (emails.moveToNext()) {

                        // This would allow you get several email addresses
                        String emailAddress = emails
                                .getString(emails
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                        //Log.e("email==>", emailAddress);

                        emaildata +=" "+emailAddress+" "
                                +"--------------------------------------";
                    }

                    emails.close();
                }

            }
            else
            {
                emaildata +=" Data not found. ";

            }
            cur.close();


        } catch (Exception e) {

            emaildata +=" Exception : "+e+" ";
        }

        return emaildata;
    }

      /*  ContentResolver cr =context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_FILTER_URI,Uri.encode(number));
        String emailId = " Email Id Not Saved ";
        Cursor cursor = cr.query(uri, null, null, null, null);

        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

        ContentResolver contentResolver = context.getContentResolver();
        Cursor emailCur = contentResolver.query( ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID
                        + " = ?", new String[] { id }, null);

        try{
            if(emailCur != null && emailCur.getCount()>0 ){
                emailCur.moveToNext();
                emailId = emailCur
                        .getString(emailCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)); }
        }
        finally {
            if (emailCur!= null) {
                emailCur.close();
            }
        }

        return emailId;
    }
*/




    private void startApp(Context context, String number, String name, String emailId){
        Intent intent=new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("number","Incoming Call "+"from "+name+ "\n number:"+number+"\n Email Id : "+emailId);
        context.startActivity(intent);
    }

    private void rejectCall(){


        try {

            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            // Disable access check
            method.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = method.invoke(telephonyManager);
            // Get the endCall method from ITelephony
            Class<?> telephonyInterfaceClass =Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}


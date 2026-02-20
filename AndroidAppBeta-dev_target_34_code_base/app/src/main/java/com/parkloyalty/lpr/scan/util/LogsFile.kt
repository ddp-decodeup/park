package com.parkloyalty.lpr.scan.util

import android.content.Context

object LogsFile {
    @JvmStatic
    fun writeFileOnInternalStorage(mcoContext: Context?, sBody: String?) {
//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.FILE_NAME );
//        String sFileName = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
//        if(!dir.exists()){
//            dir.mkdir();
//        }
//            LogUtil.printLog("SONU",sBody);
//        try {
//            File gpxfile = new File(dir, sFileName+"-logs.txt");
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(sBody);
//            writer.flush();
//            writer.close();
//        } catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText(mcoContext,e.getMessage(),Toast.LENGTH_SHORT).show();
//        }
    } /*
    protected BlockingQueue<String> blockingQueue = null;

    public LogsFile(BlockingQueue<String> blockingQueue){
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        PrintWriter writer = null;

        try {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.FILE_NAME );
            String sFileName = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            if(!dir.exists()){
                dir.mkdir();
            }

            writer = new PrintWriter( new File(dir, sFileName+"-logs.txt"));

            while(true){
                String buffer = blockingQueue.take();
                //Check whether end of file has been reached
                if(buffer.equals("EOF")){
                    break;
                }
                writer.println(buffer);
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch(InterruptedException e){

        }finally{
//            writer.close();
        }

    }*/
}
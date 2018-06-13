package wenchao.kiosk;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by ASANDA on 2018/06/13.

 */

public final class BufferWrapper {
    //HELPER CLASS FOR CONVERTING IMAGES TO BYTES AND BYTES TO BASE64.

    //ALLOCATED BUFFER
    private final byte[] buffer;

    public BufferWrapper(byte[] pBuffer){
        this.buffer = pBuffer;
    }

    public final Bitmap toBitmap(){
        return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
    }
    public final byte[] toBase64(){
         return Base64.encode(this.buffer, Base64.DEFAULT);
    }
    public final String toBase64String(){
        return new String(this.toBase64());
    }

    public static BufferWrapper fromBitmap(Bitmap map, Bitmap.CompressFormat format, boolean recycle){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        map.compress(format, 0, outStream);
        byte[] buffer = outStream.toByteArray();
        if(recycle)
            map.recycle();

        return new BufferWrapper(buffer);
    }
    public static BufferWrapper fromBase64(byte[] buffer64){
        return new BufferWrapper(Base64.decode(buffer64, Base64.DEFAULT));
    }
    public static BufferWrapper fromBase64String(String strBuffer){
        return fromBase64(strBuffer.getBytes());
    }

    public final byte[] getBuffer(){ return buffer; }

    @Override
    public String toString() {
        return "Buffer{ Size= " + buffer.length  +", type= FINAL }";
    }
}

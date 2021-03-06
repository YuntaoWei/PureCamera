package com.pure.camera.common;

import android.graphics.ImageFormat;
import android.media.Image;

import com.pure.camera.bean.MediaFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ImageUtil {

    //存储格式为YYYY...UUUUU...VVVV...
    public static final int YUV420P = 0;
    //存储格式为YYYY...UVUVUVUV...或YYYY...VUVUVUVU...
    public static final int YUV420SP = 1;
    //存储格式为YYYY...VUVUVUVU....
    public static final int NV21 = 2;
    //存储格式为YYYY...UVUVUVUV...
    public static final int NV12 = 3;
    private static final String TAG = "ImageUtil";

    public static int[] byteArray2IntArray(byte[] src) {
        int[] dst = new int[src.length];
        int i = 0;
        for (byte a : src) {
            dst[i++] = a & 0xff;
        }

        return dst;
    }

    public static byte[] getCamera2YUVData(Image image, int type) {
        if(type < NV21) {
            LogPrinter.e(TAG, "Only support NV12 and NV21 format!");
            return null;
        }

        Image.Plane planes[] = image.getPlanes();
        int w = image.getWidth();
        int h = image.getHeight();
        if(planes.length == 3) {
            Buffer yBuffer = planes[0].getBuffer();
            Buffer uvBuffer = planes[1].getBuffer();
            Buffer vuBuffer = planes[2].getBuffer();

            byte[] data = new byte[w * h * 3 / 2];
            if(type == NV21) {
                ((ByteBuffer) yBuffer).get(data, 0, yBuffer.remaining());
                ((ByteBuffer) vuBuffer).get(data, yBuffer.remaining(), vuBuffer.remaining());
            } else if(type == NV12) {
                ((ByteBuffer) yBuffer).get(data, 0, yBuffer.remaining());
                ((ByteBuffer) uvBuffer).get(data, yBuffer.remaining(), uvBuffer.remaining());
            }

            return data;
        }

        return null;
    }

    public static byte[] getYUVDataFromImageAsType(Image image, int type) {
        Image.Plane planes[] = image.getPlanes();
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            //final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            //目标数组的装填到的位置
            int dstIndex = 0;

            //临时存储uv数据的
            byte uBytes[] = new byte[width * height / 4];
            byte vBytes[] = new byte[width * height / 4];
            int uIndex = 0;
            int vIndex = 0;

            int pixelsStride, rowStride;
            for (int i = 0; i < planes.length; i++) {
                pixelsStride = planes[i].getPixelStride();
                rowStride = planes[i].getRowStride();

                ByteBuffer buffer = planes[i].getBuffer();

                //如果pixelsStride==2，一般的Y的buffer长度=640*480，UV的长度=640*480/2-1
                //源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            uBytes[uIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                } else if (i == 2) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            vBytes[vIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                }
            }

            //   image.close();

            //根据要求的结果类型进行填充
            switch (type) {
                case YUV420P:
                    //存储格式：YYYY...UUUU...VVVV
                    System.arraycopy(uBytes, 0, yuvBytes, dstIndex, uBytes.length);
                    System.arraycopy(vBytes, 0, yuvBytes, dstIndex + uBytes.length, vBytes.length);
                    break;
                case YUV420SP:
                    //存储格式：YYYY...UVUVUVUV....
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = uBytes[i];
                        yuvBytes[dstIndex++] = vBytes[i];
                    }
                    break;
                case NV21:
                    //存储格式：YYYY...VUVUVUVU....
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = vBytes[i];
                        yuvBytes[dstIndex++] = uBytes[i];
                    }
                    break;
            }
            return yuvBytes;
        } catch (final Exception e) {
            e.printStackTrace();
            LogPrinter.i("data", e.toString());
        }
        return null;
    }

    public static byte[] getYUV420Data(Image image, int type) {
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] uBytes = null;
        byte[] vBytes = null;
        Image.Plane[] planes = image.getPlanes();
        byte[] yuvBytes = new byte[width * height * 3 / 2];

        for (int i = 0; i < planes.length; i++) {
            int offset = planes[i].getRowStride();
            int cols = planes[i].getPixelStride();
            ByteBuffer bf = planes[i].getBuffer();
            if(i == 0) {
                //Y data
                bf.get(yuvBytes, 0, width * height);
            } else if(i == 1) {
                //U data
                int size;
                int capacity = bf.capacity();
                if(capacity % 2 == 1) {
                    size = (capacity + 1) / 2;
                } else {
                    size = capacity / 2;
                }
                uBytes = new byte[size];
                int x = 0;
                for(int j = 0; j < capacity;) {
                    uBytes[x++] = bf.get(j);
                    j += 2;
                }
            } else if(i == 2) {
                //V data
                int size;
                int capacity = bf.capacity();
                if(capacity % 2 == 1) {
                    size = (capacity + 1) / 2;
                } else {
                    size = capacity / 2;
                }
                vBytes = new byte[size];
                int x = 0;
                for(int j = 0; j < capacity;) {
                    vBytes[x++] = bf.get(j);
                    j += 2;
                }
            }
        }

        int dstIndex = width * height;
        if(Assert.objectNotNull(uBytes, vBytes)) {
            switch (type) {
                case YUV420P:
                    //存储格式：YYYY...UUUU...VVVV
                    System.arraycopy(uBytes, 0, yuvBytes, dstIndex, uBytes.length);
                    System.arraycopy(vBytes, 0, yuvBytes, dstIndex + uBytes.length, vBytes.length);
                    break;
                case YUV420SP:
                    //存储格式：YYYY...UVUVUVUV....
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = uBytes[i];
                        yuvBytes[dstIndex++] = vBytes[i];
                    }
                    break;
                case NV21:
                    //存储格式：YYYY...VUVUVUVU....
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = vBytes[i];
                        yuvBytes[dstIndex++] = uBytes[i];
                    }
                    break;
            }

            return yuvBytes;
        }

        return null;
    }

    /***
     * YUV420 转化成 RGB
     */
    public static int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int rgb[] = new int[frameSize];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }

    public static void debugYuvData(Image image) {
        Image.Plane[] planes = image.getPlanes();
        for (int i = 0; i < planes.length; i++) {
            ByteBuffer iBuffer = planes[i].getBuffer();
            int iSize = iBuffer.remaining();
            LogPrinter.i(TAG, "pixelStride  " + planes[i].getPixelStride());
            LogPrinter.i(TAG, "rowStride   " + planes[i].getRowStride());
            LogPrinter.i(TAG, "width  " + image.getWidth());
            LogPrinter.i(TAG, "height  " + image.getHeight());
            LogPrinter.i(TAG, "bufferSize  " + iSize);
            LogPrinter.i(TAG, "Finished reading data from plane  " + i);
        }

        LogPrinter.i(TAG, "\nAll pixel count : " + image.getWidth() * image.getHeight());
    }

    public static void debugYUV(Image image) {
        Image.Plane[] planes = image.getPlanes();
        // Y-buffer
        ByteBuffer yBuffer = planes[0].getBuffer();
        int ySize = yBuffer.remaining();
        byte[] yBytes = new byte[ySize];
        yBuffer.get(yBytes);

        // U-buffer
        ByteBuffer uBuffer = planes[1].getBuffer();
        int uSize = uBuffer.remaining();
        byte[] uBytes = new byte[uSize];
        uBuffer.get(uBytes);

        // V-buffer
        ByteBuffer vBuffer = planes[2].getBuffer();
        int vSize = vBuffer.remaining();
        byte[] vBytes = new byte[vSize];
        vBuffer.get(vBytes);

        String yFileName = "Y";
        String uFileName = "U";
        String vFileName = "V";

        // 保存目录
        File dir = new File(MediaFile.DEFAUT_STORAGE_LOCATION + File.separator + "YUVV");

        if (!dir.exists()) {
            dir.mkdir();
        }

        // 文件名
        File yFile = new File(dir.getAbsolutePath() + File.separator + yFileName + ".yuv");
        File uFile = new File(dir.getAbsolutePath() + File.separator + uFileName + ".yuv");
        File vFile = new File(dir.getAbsolutePath() + File.separator + vFileName + ".yuv");


        try {

            // 以字符方式书写
            Writer yW = new FileWriter(yFile);
            Writer uW = new FileWriter(uFile);
            Writer vW = new FileWriter(vFile);

            for (int i = 0; i < ySize; i++) {

                String preValue = Integer.toHexString(yBytes[i]); // 转为16进制
                // 因为byte[] 元素是一个字节，这里只取16进制的最后一个字节
                String lastValue = preValue.length() > 2 ? preValue.substring(preValue.length() - 2) : preValue;
                yW.write(" " + lastValue + " "); // 写入文件
                if ((i + 1) % 20 == 0) {  // 每行20个
                    yW.write("\n");
                }
            }
            yW.close();


            for (int i = 0; i < uSize; i++) {
                String preValue = Integer.toHexString(uBytes[i]);
                String lastValue = preValue.length() > 2 ? preValue.substring(preValue.length() - 2) : preValue;
                uW.write(" " + lastValue + " ");
                if ((i + 1) % 20 == 0) {
                    uW.write("\n");
                }
            }
            uW.close();


            for (int i = 0; i < vSize; i++) {
                String preValue = Integer.toHexString(vBytes[i]);
                String lastValue = preValue.length() > 2 ? preValue.substring(preValue.length() - 2) : preValue;
                vW.write(" " + lastValue + " ");
                if ((i + 1) % 20 == 0) {
                    vW.write("\n");
                }
            }
            vW.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

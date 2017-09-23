/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

package omada17.newyorkapp;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class LoadImage extends Activity
{
    private String urls[];

    private class GetImage extends AsyncTask<String, Void, Bitmap> {
        ImageView view;

        public GetImage(ImageView v) {
            view = v;
        }

        protected Bitmap doInBackground(String... urls) {
            String s = urls[0];
            Bitmap bmp = null;
            try {

                URL url = new URL(s);

                InputStream in = url.openConnection().getInputStream();

                BufferedInputStream bin = new BufferedInputStream(in,1024*8);
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int len=0;
                byte[] b = new byte[1024];
                while((len = bin.read(b)) != -1){
                    out.write(b, 0, len);
                }

                out.close();
                bin.close();

                byte[] data = out.toByteArray();
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap bmp) {

            view.setImageBitmap(bmp);
        }
    }
    private int img = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadimage);

        urls = getIntent().getStringArrayExtra("urls");
        for(int i=0; i<urls.length;i++)
            System.out.println(urls[i]);

        String info = getIntent().getStringExtra("info");
        ((TextView) findViewById(R.id.imageinfo)).setText(info);

        setCurrentImage();


        Button next = (Button) findViewById(R.id.nextimage);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                img++;
                if(img == urls.length) {
                    img = 0;
                }
                setCurrentImage();
            }
        });

    }


    private void setCurrentImage() {

        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        GetImage g = new GetImage(imageView);

        System.out.println(urls[img]);

        g.execute(urls[img]);
    }
}
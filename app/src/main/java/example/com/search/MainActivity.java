package example.com.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    StringBuilder searchResult;
    BufferedReader br;
    String[] title, link, description, bloggername, postdate;
    SNSViewAdaptor mMyAdapter;

    private ListView mListView;
    int itemCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //손댐



        searchNaver("동명동");//검색내용

        mListView = (ListView) findViewById(R.id.listViewSNS);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String blog_url = mMyAdapter.getItem(position).getBloglink();
                Intent intent = new Intent(getApplicationContext(), InternetWebView.class);
                intent.putExtra("blog_url", blog_url);
                startActivity(intent);

            }
        });


    }



    public void searchNaver(final String searchObject) {
        final String clientId = "zml2PliJFsjLaSIrtVU1";//애플리케이션 클라이언트 아이디값";
        final String clientSecret = "c7M3SU1Ed_";//애플리케이션 클라이언트 시크릿값";
        final int display = 10; // 보여지는 검색결과의 수

        // 네트워크 연결은 Thread 생성 필요
        new Thread() {

            @Override
            public void run() {
                try {
                    String text = URLEncoder.encode(searchObject, "UTF-8");
                    String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text + "&display=" + display + "&"; // json 결과

                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    con.connect();

                    int responseCode = con.getResponseCode();


                    if(responseCode==200) { // 정상 호출
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }

                    searchResult = new StringBuilder();
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        searchResult.append(inputLine + "\n");

                    }
                    br.close();
                    con.disconnect();

                    String data = searchResult.toString();

                    String[] array = data.split("\"");
                    title = new String[display];
                    link = new String[display];
                    description = new String[display];
                    bloggername = new String[display];
                    postdate = new String[display];

                    itemCount = 0;
                    for (int i = 0; i < array.length; i++) {
                        if (array[i].equals("title"))
                            title[itemCount] = array[i + 2];
                        if (array[i].equals("link"))
                            link[itemCount] = array[i + 2];
                        if (array[i].equals("description"))
                            description[itemCount] = array[i + 2];
                        if (array[i].equals("bloggername"))
                            bloggername[itemCount] = array[i + 2];
                        if (array[i].equals("postdate")) {
                            postdate[itemCount] = array[i + 2];
                            itemCount++;
                        }
                    }

                    Log.d(TAG, "title잘나오니: " + title[0] + title[1] + title[2]);

                    // 결과를 성공적으로 불러오면, UiThread에서 listView에 데이터를 추가
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listViewDataAdd();
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "error : " + e);
                }

            }
        }.start();

    }


    public void listViewDataAdd() {
        mMyAdapter = new SNSViewAdaptor();
        for (int i = 0; i < itemCount; i++) {

            mMyAdapter.addItem(Html.fromHtml(title[i]).toString(),
                    Html.fromHtml(description[i]).toString(),
                    Html.fromHtml(bloggername[i]).toString(),
                    Html.fromHtml(postdate[i]).toString(),
                    Html.fromHtml(link[i]).toString());

        }

        // set adapter on listView
        mListView.setAdapter(mMyAdapter);

    }




}

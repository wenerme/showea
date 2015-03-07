package me.wener.showea.collect.qq;

import static java.lang.ClassLoader.getSystemResourceAsStream;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.util.List;
import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import me.wener.showea.collect.NicePrint;
import me.wener.showea.model.ZoneMessage;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.TxMaker;

@Ignore
public class ZoneMessageCollectorTest
{
    @Test
    public void test() throws IOException
    {

        String json = IOUtils.toString(getSystemResourceAsStream("qq/zone-msg.json"), Charsets.UTF_8);
        List<ZoneMessage> messages = ZoneMessageCollector
                .parse(json);

        NicePrint.zoneMessage(messages);
        System.out.println("Total: " + ZoneMessageCollector.getTotal(json));
    }

    @Test
    public void testHttp()
    {
        HttpRequest request = HttpRequest.get(ZoneMessageCollector.getMessageURL("514403150", 20, 40));
        HttpResponse response = request.send();

        List<ZoneMessage> messages = ZoneMessageCollector.parse(response.bodyText());

        NicePrint.zoneMessage(messages);
        System.out.println("Total: " + messages.size());
    }

    @Test
    public void testBrowser()
    {
        HttpBrowser browser = new HttpBrowser();

        HttpRequest request = HttpRequest.get("m.qzone.qq.com");
        browser.sendRequest(request);

        // request is sent and response is received

        // process the page:
        String page = browser.getPage();

        System.out.println(page);

        // create new request
//        HttpRequest newRequest = HttpRequest.post(formAction);
//
//        browser.sendRequest(newRequest);
    }

    @Test
    public void testUrl()
    {
        DBMaker maker = DBMaker.newFileDB(new File("showea.data"))
                               .cacheLRUEnable()
                               .cacheSize(100)
                               .checksumEnable();
        DB db = maker.make();
        TxMaker txMaker = maker.makeTxMaker();

        BTreeMap<Object, Object> map = db.getTreeMap("zone-message:514403150");

    }

}

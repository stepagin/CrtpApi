import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // Конвертер в JSON с указанным форматом даты и времени
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        // Конструктор по заданию
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
    }

    public void makeApiRequest(Document document) {
        synchronized (this) {
            if (requestCount.get() < requestLimit) {
                requestCount.incrementAndGet();
                String jsonString = gson.toJson(document);
                doApiRequest(jsonString);
            } else {
                System.out.println("Request limit exceeded. Please try again later.");
            }
        }
    }

    private void doApiRequest(String body) {
        // Отправка запроса к API Честного знака
        String httpsURL = "https://ismp.crpt.ru/api/v3/lk/documents/create";

        try {
            URL url = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);

            DataOutputStream output = new DataOutputStream(con.getOutputStream());
            output.writeBytes(body);
            output.close();
            DataInputStream input = new DataInputStream(con.getInputStream());
            System.out.println((char) input.read());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void startRequestLimitResetTask() {
        // задача сброса счётчика запросов
        // выполняется один раз за указанное в конструкторе время
        scheduler.scheduleAtFixedRate(this::resetRequestCount, 0, timeUnit.toMillis(1), timeUnit.MILLISECONDS);
    }

    private void resetRequestCount() {
        // сброс счётчика запросов
        requestCount.set(0);
    }

    public static void main(String[] args) {
        // Тестовый документ из ТЗ
        Document document = Document.makeTestDocument();
        // Пример создания объекта CrptApi
        CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 100);
        // Запускаем задачу сброса счетчика запросов
        crptApi.startRequestLimitResetTask();
        // Вызов метода для выполнения запроса к API
        crptApi.makeApiRequest(document);

    }
}


@Getter
@Setter
@RequiredArgsConstructor
class Description {
    /*
    Класс описания с использованием Lombok
     */
    private String participantInn;
}

@Getter
@Setter
@RequiredArgsConstructor
class Product {
    /*
    Класс продукта с использованием Lombok
     */
    private String certificate_document;
    private Date certificate_document_date;
    private String certificate_document_number;
    private String owner_inn;
    private String producer_inn;
    private Date production_date;
    private String tnved_code;
    private String uit_code;
    private String uitu_code;
}


@Getter
@Setter
@RequiredArgsConstructor
class Document {
    /*
    Класс документа с использованием Lombok
     */
    private Description description;
    private List<Product> products;
    private String doc_id;
    private String doc_status;
    private String doc_type;
    private Boolean importRequest;
    private String owner_inn;
    private String participant_inn;
    private String producer_inn;
    private Date production_date;
    private String production_type;
    private Date reg_date;
    private String reg_number;

    public static Document makeTestDocument() {
        /*
        Метод, создающий тестовый документ из ТЗ
         */
        Description description = new Description();
        description.setParticipantInn("string");

        Product product = new Product();
        product.setCertificate_document("string");
        product.setCertificate_document_date(new Date());
        product.setCertificate_document_number("string");
        product.setOwner_inn("string");
        product.setProducer_inn("string");
        product.setProduction_date(new Date());
        product.setTnved_code("string");
        product.setUit_code("string");
        product.setUitu_code("string");

        Document document = new Document();
        document.setDescription(description);
        document.setDoc_id("string");
        document.setDoc_status("string");
        document.setDoc_type("LP_INTRODUCE_GOODS");
        document.setImportRequest(true);
        document.setOwner_inn("string");
        document.setParticipant_inn("string");
        document.setProducer_inn("string");
        document.setProduction_date(new Date());
        document.setProduction_type("string");
        document.setProducts(List.of(new Product[]{product}));
        document.setReg_date(new Date());
        document.setReg_number("string");

        return document;
    }
}


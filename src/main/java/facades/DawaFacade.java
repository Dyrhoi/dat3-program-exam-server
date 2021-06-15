package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.external.DawaDto;
import utils.HttpUtils;

import javax.ws.rs.WebApplicationException;
import java.util.concurrent.*;

public class DawaFacade {
    private static final String DAWA_URL = "https://api.dataforsyningen.dk";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static DawaDto getDawaByAddressId(String id){
        String path = "/adresser/" + id;
        String url = DAWA_URL + path;

        return getDawaInformation(url);
    }

    private static DawaDto getDawaInformation(String apiQueryUrl) {
        // Modify to minified response.
        apiQueryUrl = apiQueryUrl + (apiQueryUrl.contains("?") ? "&struktur=mini" : "?struktur=mini");


        ExecutorService executor = Executors.newCachedThreadPool();

        // Required...
        String finalApiQueryUrl = apiQueryUrl;

        Callable<String> getDataFromApi = () -> HttpUtils.fetchData(finalApiQueryUrl);
        Future<String> future = executor.submit(getDataFromApi);

        DawaDto dawaDTO;

        try{
            String json = future.get();
            dawaDTO = GSON.fromJson(json, DawaDto.class);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new WebApplicationException("Error when connecting to external API", 500);
        }

        executor.shutdown();
        return dawaDTO;
    }
}

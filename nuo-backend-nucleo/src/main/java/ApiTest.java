/**
 * Copyright 2015-2018 NuoCanvas.
 * <p>
 * <p>
 * Created by Pulkit on 11Jan2017
 * <p>
 * Content of this file is proprietary and confidential.
 * It shall not be reused or disclosed without prior consent
 * of distributor
 **/

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class ApiTest {

    public static Bigquery createAuthorizedClient() throws IOException {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("E:\\NuoCanvas\\BigQueryPoC-c46f28aab6b8.json"));
//                .Builder().setClientSecrets("313665007560-gcl9ldhel6v8g4p63ar47qdf6acmkif8.apps.googleusercontent.com",
//                "oFKn3iTVPROkuAsO8K1BSWQQ").build();

        if (credential.createScopedRequired()) {
            credential = credential.createScoped(BigqueryScopes.all());
        }

        return new Bigquery.Builder(transport, jsonFactory, credential)
                .setApplicationName("Bigquery Samples")
                .build();
    }

    private static List<TableRow> executeQuery(String querySql, Bigquery bigquery, String projectId)
            throws IOException {


        QueryRequest queryReq = new QueryRequest()
                .setQuery(querySql)
                .setUseLegacySql(false)
                .setUseQueryCache(true);

        QueryResponse query =
                bigquery.jobs().query(projectId,queryReq).execute();

        // Execute it
        GetQueryResultsResponse queryResult =
                bigquery
                        .jobs()
                        .getQueryResults(
                                query.getJobReference().getProjectId(), query.getJobReference().getJobId())
                        .execute();

        return queryResult.getRows();
    }

    private static void printResults(List<TableRow> rows) {
        System.out.print("\nQuery Results:\n------------\n");
        for (TableRow row : rows) {
            for (TableCell field : row.getF()) {
                System.out.printf("%-50s", field.getV());
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc;
        if (args.length == 0) {
            sc = new Scanner(System.in);
        } else {
            sc = new Scanner(args[0]);
        }
        String projectId="geometric-watch-153714";

        Bigquery bigquery = createAuthorizedClient();

        List<TableRow> rows =
                executeQuery(
                        "SELECT corpus as unique_words "
                                + "FROM [bigquery-public-data:samples.shakespeare] LIMIT 10",
                        bigquery,
                        projectId);

        printResults(rows);
    }
}
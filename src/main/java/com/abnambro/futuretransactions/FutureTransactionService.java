package com.abnambro.futuretransactions;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FutureTransactionService {

    final Logger LOGGER = LoggerFactory.getLogger(FutureTransactionService.class);
    public static final char CSV_SEPARATOR = ',';

    @Value("${input_path}")
    String inputPath;

    @Value("${output_path}")
    String outputPathString;

    @Value("${input_file}")
    String inputFile;

    @Value("${output_file}")
    String outputFile;

    @Value("${client_information.client_type_start_index}")
    Integer clientTypeStartIndex;

    @Value("${client_information.client_type_end_index}")
    Integer clientTypeEndIndex;

    @Value("${client_information.client_number_start_index}")
    Integer clientNumberStartIndex;

    @Value("${client_information.client_number_end_index}")
    Integer clientNumberEndIndex;

    @Value("${client_information.account_number_start_index}")
    Integer accountNumberStartIndex;

    @Value("${client_information.account_number_end_index}")
    Integer accountNumberEndIndex;

    @Value("${client_information.subaccount_number_start_index}")
    Integer subAccountNumberStartIndex;

    @Value("${client_information.subaccount_number_end_index}")
    Integer subAccountNumberEndIndex;

    @Value("${product_information.product_code_start_index}")
    Integer productCodeStartIndex;

    @Value("${product_information.product_code_end_index}")
    Integer productCodeEndIndex;

    @Value("${product_information.exchange_code_start_index}")
    Integer exchangeCodeStartIndex;

    @Value("${product_information.exchange_code_end_index}")
    Integer exchangeCodeEndIndex;

    @Value("${product_information.symbol_start_index}")
    Integer symbolStartIndex;

    @Value("${product_information.symbol_end_index}")
    Integer symbolEndIndex;

    @Value("${product_information.expiration_date_start_index}")
    Integer expirationDateStartIndex;

    @Value("${product_information.expiration_date_end_index}")
    Integer expirationDateEndIndex;

    @Value("${transaction_information.qantity_long_start_index}")
    Integer qantityLongStartIndex;

    @Value("${transaction_information.qantity_long_end_index}")
    Integer qantityLongEndIndex;

    @Value("${transaction_information.qantity_short_start_index}")
    Integer qantityShortStartIndex;

    @Value("${transaction_information.qantity_short_end_index}")
    Integer qantityShortEndIndex;

    @Value("${transaction_information.transaction_date_start_index}")
    Integer transactionDateStartIndex;

    @Value("${transaction_information.transaction_date_end_index}")
    Integer transactionDateEndIndex;

    /**
     * This Function gives the CSV on demand report of all the transactions in the input.txt
     * @param response
     * @throws IOException
     */
    public void getAllTransaction(HttpServletResponse response) throws IOException {

        final Path path = Paths.get(inputPath);
        final Path outputPath = Paths.get(outputPathString);
        final Path txt = path.resolve(inputFile);
        final Path csv = outputPath.resolve(outputFile);
        String line;
        List<FutureTransaction> futureTransactions =  new ArrayList<>();
        try {
            BufferedReader reader = Files.newBufferedReader(txt);
            while ((line = reader.readLine()) != null) {
                /**
                 * Calling getTransaction function which gets the fields
                 * required for the report from input file
                 */
                FutureTransaction futureTransaction = getTransaction(line);
                futureTransactions.add(futureTransaction);
            }
        } catch (IOException e){
            e.printStackTrace();
            LOGGER.error("Error occurred locating input file", e);
            throw new BusinessException(
                    "BusinessException: Input file was not present.", e);
        }

        /**
         * Grouping of each transactions based on client information and product information for each day
         */
        List<FutureTransaction> transactionsDaily =  new ArrayList<>();
        futureTransactions.stream()
             .collect(Collectors
                     .groupingBy(
                             transaction -> new FutureTransaction(transaction.getClientInformation(), transaction.getProductInformation(), transaction.getTransactionDate()),
                             Collectors.summarizingInt(user -> user.getTotalTransactionAmount())
                     )
             ).forEach((k,v) -> {
                 k.setTotalTransactionAmount((int) v.getSum());
                 transactionsDaily.add(k);
             });

        /**
         * Converting the transactionsDaily list to CSV
         */
        getCSVResponse(response, csv, transactionsDaily);

        /**
         * creating output.csv from transactionsDaily
         */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPathString+outputFile))) {
            writer.append("Client_Information").append(CSV_SEPARATOR)
                  .append("Product_Information").append(CSV_SEPARATOR)
                  .append("Total_Transaction").append(System.lineSeparator());
            transactionsDaily.forEach(transaction -> {
                try {
                    writer.append(transaction.getClientInformation()).append(CSV_SEPARATOR)
                          .append(transaction.getProductInformation()).append(CSV_SEPARATOR)
                          .append(transaction.getTotalTransactionAmount().toString())
                          .append(System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.error("Error occurred while writing data into output.csv", e);
                    throw new RuntimeException(
                            "BusinessException: even creation is not possible.", e);
                }
            });
            LOGGER.info("Future transactions output file successfully generated");
        } catch (IOException ex) {
            ex.printStackTrace();
            LOGGER.error("Error occurred locating output directory", ex);
            throw new BusinessException(
                    "BusinessException: output file was not present.", ex);
        }

    }

    /**
     * getCSVResponse is to create CSV response for the API
     * @param response
     * @param csv
     * @param transactionsDaily
     * @throws IOException
     */
    private void getCSVResponse(HttpServletResponse response, Path csv, List<FutureTransaction> transactionsDaily) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + csv + "\"");

        CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(),
                CSVFormat.DEFAULT.withHeader("Client_Information", "Product_Information", "Total_Transaction"));
        for (FutureTransaction transaction : transactionsDaily) {
            csvPrinter.printRecord(Arrays.asList(transaction.getClientInformation(), transaction.getProductInformation(), transaction.getTotalTransactionAmount()));
        }
    }

    /**
     * Takes each line as input and returns FutureTransaction
     * @param line
     * @return FutureTransaction
     */
    private FutureTransaction getTransaction(String line) {
        FutureTransaction futureTransaction = new FutureTransaction();
        futureTransaction.setClientInformation(
                line.substring(clientTypeStartIndex, clientTypeEndIndex) +""+
                        line.substring(clientNumberStartIndex, clientNumberEndIndex) +""+
                        line.substring(accountNumberStartIndex, accountNumberEndIndex) +""+
                        line.substring(subAccountNumberStartIndex, subAccountNumberEndIndex));
        futureTransaction.setProductInformation(
                line.substring(productCodeStartIndex, productCodeEndIndex)+""+
                        line.substring(exchangeCodeStartIndex, exchangeCodeEndIndex)+""+
                        line.substring(symbolStartIndex, symbolEndIndex)+""+
                        line.substring(expirationDateStartIndex, expirationDateEndIndex)
        );
        futureTransaction.setTotalTransactionAmount(
                Integer.parseInt(line.substring(qantityLongStartIndex, qantityLongEndIndex)) -
                        Integer.parseInt(line.substring(qantityShortStartIndex, qantityShortEndIndex))
        );
        futureTransaction.setTransactionDate(line.substring(transactionDateStartIndex, transactionDateEndIndex));
        return futureTransaction;
    }

    /**
     * This function is scheduled daily at 6A.M to generate output.csv
     * @throws IOException
     */
    @Scheduled(cron = "${transactions.daily-cron}")
    public void getDailyTransaction() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        getAllTransaction(response);

    }
}


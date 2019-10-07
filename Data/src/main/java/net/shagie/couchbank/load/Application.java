package net.shagie.couchbank.load;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final long MAX_ACC = 400_000;

    public static void main(String... args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            log.info("start");
            HttpClient httpClient = new StdHttpClient.Builder()
                    .url("http://localhost:5984")
                    .build();

            CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
            CouchDbConnector accountsDb = new StdCouchDbConnector("accounts", dbInstance);
            CouchDbConnector transDb = new StdCouchDbConnector("transactions", dbInstance);

            for (int i = 0; i < 400; i++) {
                log.info(String.format("accounts batch %d", i));
                List<Account> accounts = LongStream.range(i * 1000, (i + 1) * 1000)
                        .mapToObj(l -> new Tuple<>(l, new BeTwoSix(l)))
                        .map(tuple -> {
                            Account rv = new Account();
                            rv.setId(String.format("%06d", tuple.getFirst()))
                                    .setLastName(tuple.getSecond().asString().substring(0, 2))
                                    .setFirstName(tuple.getSecond().asString().substring(2, 4));
                            return rv;
                        })
                        .collect(Collectors.toList());
                accountsDb.executeBulk(accounts);
            }

            AtomicLong transId = new AtomicLong(0);
            PoissonDistribution pd = new PoissonDistribution(5.0);
            Random rnd = new Random();

            List<Transaction> transactions = LongStream.range(0, MAX_ACC)
                    .mapToObj(l -> new Tuple<>(l, pd.sample()))
                    .map(tuple -> {
                        List<Transaction> tList = new ArrayList<>(tuple.getSecond());
                        for (int i = 0; i < tuple.getSecond(); i++) {
                            BigDecimal bdAmt;
                            if (i == 0) {
                                long amt = Math.abs(rnd.nextLong()) % 1_000_00;
                                amt += 100_00;
                                bdAmt = BigDecimal.valueOf(amt, 2);
                            } else {
                                long amt = Math.abs(rnd.nextLong()) % 100_00;
                                amt -= 50_00;
                                bdAmt = BigDecimal.valueOf(amt, 2);
                            }
                            Transaction t = new Transaction();
                            t.setId(Long.toString(transId.getAndIncrement()));
                            t.setAccountId(String.format("%06d", tuple.getFirst()));
                            t.setAmount(bdAmt);
                            tList.add(t);
                        }
                        return tList;
                    })
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            ListUtils.partition(transactions, 1000).forEach(transDb::executeBulk);
            log.info("done");
        };
    }

}

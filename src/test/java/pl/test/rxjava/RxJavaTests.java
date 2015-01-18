package pl.test.rxjava;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class RxJavaTests {
	
	@Test
	public void testCreateObservableFromList() {
		List<String> lista = Arrays.asList("A", "B", "C", "D");
		Observable<String> observable = Observable.from(lista);
		observable.subscribe(System.out::println);
	}
	
	@Test
	public void testCreateObservableFromRange() {
		Observable<Integer> observable = Observable.range(1, 20);
		observable.subscribe(System.out::println);
	}
	
	@Test
	public void testCreateObservableFromRangeFilter() {
		Observable<Integer> observable = Observable.range(1, 20).filter(i -> (i > 10));
		observable.subscribe(System.out::println);
	}
	
	@Test
	public void testCreateObservableFromAsyncRestCall() {
		JerseyClient client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://www.google.pl");
		Observable<String> observable = Observable.from(target.request().async().get(String.class));
		observable.subscribe(
				//onNext
				System.out::println,
				//onError
				i -> {System.out.println("Error: "+i);});
	}
	
	
	@Test
	public void testCreateObservableFromAsyncRestCallAsynchr() throws Exception {
		JerseyClient client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://www.google.pl");
		Observable<String> observable = Observable.
											from(target.request().async().get(String.class)).
											subscribeOn(Schedulers.io());
		Subscription sub = observable.subscribe(
                //onNext
                System.out::println,
                //onError
                i -> {
                    System.out.println("Error: " + i);
                });


		//Thread.sleep(10000);
	}
	
	@Test
	public void testCreateObservableFromRangeOnThread() {
		System.out.println(Thread.currentThread().getName());
		Observable<Integer> observable = Observable.range(1, 20).observeOn(Schedulers.computation());
		observable.subscribe(
				i ->  System.out.println(i + " Thread: " + Thread.currentThread().getName()));
	}
	
	
	@Test
	public void testTimeTickerSampling() throws Exception {
		TimeTicker ticker = new TimeTicker();
		ticker.toObservable().
			sample(5, TimeUnit.SECONDS).
			subscribe(i -> System.out.println("sampled ticker " + i));
		
		ticker.toObservable().subscribe(i -> System.out.println("real ticker " + i));
		Thread.sleep(20000);
	}
	
	@Test
	public void testTimeTickerBuffering() throws Exception {
		TimeTicker ticker = new TimeTicker();
		ticker.toObservable().buffer(5, TimeUnit.SECONDS).
			subscribe(System.out::println);
		Thread.sleep(20000);
	}
	
	@Test
	public void testZipingRestCalls() throws Exception {
		JerseyClient client = JerseyClientBuilder.createClient();
		WebTarget google = client.target("http://www.google.pl");
		WebTarget wppl = client.target("http://wp.pl");
		
		Observable<String> googleObs = Observable.from(google.request().async().get(String.class)).subscribeOn(Schedulers.io());
		Observable<String> wpplObs = Observable.from(wppl.request().async().get(String.class)).subscribeOn(Schedulers.io());
		
		Observable.zip(googleObs, wpplObs, (i,j) -> new String [] {i,j}).subscribe(i -> {
			System.out.println("-------------------google --------------------------");
			System.out.println(i[0].substring(0, 500));
			System.out.println("-------------------wp.pl --------------------------");
			System.out.println(i[1].substring(0, 500));
		});
		
		Thread.sleep(15000);	
	}
	
	@Test
	public void testFlatMapObservables() throws Exception {
		JerseyClient client = JerseyClientBuilder.createClient();
		WebTarget google = client.target("http://www.google.pl");
		WebTarget wppl = client.target("http://wp.pl");
		
		Observable<String> googleObs = Observable.from(google.request().async().get(String.class)).subscribeOn(Schedulers.io());
		Observable<String> wpplObs = Observable.from(wppl.request().async().get(String.class)).subscribeOn(Schedulers.io());
		
		googleObs.flatMap(i -> wpplObs).subscribe(i-> System.out.println("wpplObs"), System.out::println);
		

		Thread.sleep(15000);
	}
	
}

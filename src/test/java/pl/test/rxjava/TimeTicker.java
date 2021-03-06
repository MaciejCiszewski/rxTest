package pl.test.rxjava;


import rx.Observable;
import rx.subjects.BehaviorSubject;

public class TimeTicker {
	
	BehaviorSubject<Long> tickerSubject = BehaviorSubject.create(1000L);
	
	public TimeTicker() {
		new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						System.out.println(e);

					}
					tickerSubject.onNext(2500L);
				}
			}
		}.start();
	}
	
	public Observable<Long> toObservable() {
		return tickerSubject;
	}
 	
}

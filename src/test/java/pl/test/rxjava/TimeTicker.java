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
						Thread.sleep(2000);
					} catch (Exception e) {
						System.out.println(e);

					}
					tickerSubject.onNext(System.currentTimeMillis());
				}
			}
		}.start();
	}
	
	public Observable<Long> toObservable() {
		return tickerSubject;
	}
 	
}

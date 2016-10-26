package de.htwg.util.observer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ObservableTest {

   private boolean ping = false;
   private TestObserver testObserver;
   private Observable testObservable;

   class TestObserver implements IObserver {

      // @Override
      public void update() {
         ping = true;
      }

   }

   @Before
   public void setUp() throws Exception {
      testObserver = new TestObserver();
      testObservable = new Observable();
      testObservable.addObserver(testObserver);
   }

   @Test
   public void testNotify() {
      Assert.assertFalse(ping);
      testObservable.notifyObservers();
      Assert.assertTrue(ping);
   }

   @Test
   public void testRemove() {
      Assert.assertFalse(ping);
      testObservable.removeObserver(testObserver);
      testObservable.notifyObservers();
      Assert.assertFalse(ping);
   }

   @Test
   public void testRemoveAll() {
      Assert.assertFalse(ping);
      testObservable.removeAllObservers();
      testObservable.notifyObservers();
      Assert.assertFalse(ping);
   }

}

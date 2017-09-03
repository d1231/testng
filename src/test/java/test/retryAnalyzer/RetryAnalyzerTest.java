package test.retryAnalyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import test.SimpleBaseTest;
import test.retryAnalyzer.github1519.MyListener;
import test.retryAnalyzer.github1519.TestClassSample;

public class RetryAnalyzerTest extends SimpleBaseTest {
    @Test
    public void testInvocationCounts() {
        TestNG tng = create(InvocationCountTest.class);
        TestListenerAdapter tla = new TestListenerAdapter();
        tng.addListener(tla);

        tng.run();

        assertFalse(tng.hasFailure());
        assertFalse(tng.hasSkip());

        assertTrue(tla.getFailedTests().isEmpty());

        List<ITestResult> fsp = tla.getFailedButWithinSuccessPercentageTests();
        assertEquals(fsp.size(), 1);
        assertEquals(fsp.get(0).getName(), "failAfterThreeRetries");

        List<ITestResult> skipped = tla.getSkippedTests();
        assertEquals(skipped.size(), InvocationCountTest.invocations.size() - fsp.size());
    }

    @Test
    public void testIfRetryIsInvokedBeforeListener() {
        TestNG tng = create(TestClassSample.class);
        tng.addListener((ITestNGListener)new MyListener());
        tng.run();
        assertThat(TestClassSample.messages).containsExactly("afterInvocation", "retry", "afterInvocation");
    }
}

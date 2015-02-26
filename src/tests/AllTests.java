package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//main creates all of the lower test classes and calls runall

@RunWith(Suite.class)
@SuiteClasses({BetFairCoreTest.class})
public class AllTests
{

}

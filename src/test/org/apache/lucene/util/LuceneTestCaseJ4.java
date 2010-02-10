begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|ConcurrentMergeScheduler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldCache
operator|.
name|CacheEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FieldCacheSanityChecker
operator|.
name|Insanity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestWatchman
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Base class for all Lucene unit tests, Junit4 variant.  * Replaces LuceneTestCase.  *<p>  *</p>  *<p>  * If you  * override either<code>setUp()</code> or  *<code>tearDown()</code> in your unit test, make sure you  * call<code>super.setUp()</code> and  *<code>super.tearDown()</code>  *</p>  *  * @After - replaces setup  * @Before - replaces teardown  * @Test - any public method with this annotation is a test case, regardless  * of its name  *<p/>  *<p/>  * See Junit4 documentation for a complete list of features at  * http://junit.org/junit/javadoc/4.7/  *<p/>  * Import from org.junit rather than junit.framework.  *<p/>  * You should be able to use this class anywhere you used LuceneTestCase  * if you annotate your derived class correctly with the annotations above  * @see assertSaneFieldCaches  *<p/>  */
end_comment

begin_comment
comment|// If we really need functionality in runBare override from LuceneTestCase,
end_comment

begin_comment
comment|// we can introduce RunBareWrapper and override runChild, and add the
end_comment

begin_comment
comment|// @RunWith annotation as below. runChild will be called for
end_comment

begin_comment
comment|// every test. But the functionality we used to
end_comment

begin_comment
comment|// get from that override is provided by InterceptTestCaseEvents
end_comment

begin_comment
comment|//@RunWith(RunBareWrapper.class)
end_comment

begin_class
DECL|class|LuceneTestCaseJ4
specifier|public
class|class
name|LuceneTestCaseJ4
extends|extends
name|TestWatchman
block|{
comment|/** Change this when development starts for new Lucene version: */
DECL|field|TEST_VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|TEST_VERSION_CURRENT
init|=
name|Version
operator|.
name|LUCENE_31
decl_stmt|;
comment|// This is how we get control when errors occur.
comment|// Think of this as start/end/success/failed
comment|// events.
annotation|@
name|Rule
DECL|field|intercept
specifier|public
name|InterceptTestCaseEvents
name|intercept
init|=
operator|new
name|InterceptTestCaseEvents
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|method|LuceneTestCaseJ4
specifier|public
name|LuceneTestCaseJ4
parameter_list|()
block|{   }
DECL|method|LuceneTestCaseJ4
specifier|public
name|LuceneTestCaseJ4
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ConcurrentMergeScheduler
operator|.
name|setTestMode
argument_list|()
expr_stmt|;
name|seed
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Forcible purges all cache entries from the FieldCache.    *<p>    * This method will be called by tearDown to clean up FieldCache.DEFAULT.    * If a (poorly written) test has some expectation that the FieldCache    * will persist across test methods (ie: a static IndexReader) this    * method can be overridden to do nothing.    *</p>    *    * @see FieldCache#purgeAllCaches()    */
DECL|method|purgeFieldCache
specifier|protected
name|void
name|purgeFieldCache
parameter_list|(
specifier|final
name|FieldCache
name|fc
parameter_list|)
block|{
name|fc
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
DECL|method|getTestLabel
specifier|protected
name|String
name|getTestLabel
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// this isn't as useful as calling directly from the scope where the
comment|// index readers are used, because they could be gc'ed just before
comment|// tearDown is called.
comment|// But it's better then nothing.
name|assertSaneFieldCaches
argument_list|(
name|getTestLabel
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ConcurrentMergeScheduler
operator|.
name|anyUnhandledExceptions
argument_list|()
condition|)
block|{
comment|// Clear the failure so that we don't just keep
comment|// failing subsequent test cases
name|ConcurrentMergeScheduler
operator|.
name|clearUnhandledExceptions
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"ConcurrentMergeScheduler hit unhandled exceptions"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|purgeFieldCache
argument_list|(
name|FieldCache
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Asserts that FieldCacheSanityChecker does not detect any    * problems with FieldCache.DEFAULT.    *<p>    * If any problems are found, they are logged to System.err    * (allong with the msg) when the Assertion is thrown.    *</p>    *<p>    * This method is called by tearDown after every test method,    * however IndexReaders scoped inside test methods may be garbage    * collected prior to this method being called, causing errors to    * be overlooked. Tests are encouraged to keep their IndexReaders    * scoped at the class level, or to explicitly call this method    * directly in the same scope as the IndexReader.    *</p>    *    * @see FieldCacheSanityChecker    */
DECL|method|assertSaneFieldCaches
specifier|protected
name|void
name|assertSaneFieldCaches
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|)
block|{
specifier|final
name|CacheEntry
index|[]
name|entries
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getCacheEntries
argument_list|()
decl_stmt|;
name|Insanity
index|[]
name|insanity
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|insanity
operator|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|dumpArray
argument_list|(
name|msg
operator|+
literal|": FieldCache"
argument_list|,
name|entries
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|assertEquals
argument_list|(
name|msg
operator|+
literal|": Insane FieldCache usage(s) found"
argument_list|,
literal|0
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
name|insanity
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
comment|// report this in the event of any exception/failure
comment|// if no failure, then insanity will be null anyway
if|if
condition|(
literal|null
operator|!=
name|insanity
condition|)
block|{
name|dumpArray
argument_list|(
name|msg
operator|+
literal|": Insane FieldCache usage(s)"
argument_list|,
name|insanity
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Convinience method for logging an iterator.    *    * @param label  String logged before/after the items in the iterator    * @param iter   Each next() is toString()ed and logged on it's own line. If iter is null this is logged differnetly then an empty iterator.    * @param stream Stream to log messages to.    */
DECL|method|dumpIterator
specifier|public
specifier|static
name|void
name|dumpIterator
parameter_list|(
name|String
name|label
parameter_list|,
name|Iterator
name|iter
parameter_list|,
name|PrintStream
name|stream
parameter_list|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|"*** BEGIN "
operator|+
name|label
operator|+
literal|" ***"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|iter
condition|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|" ... NULL ..."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|stream
operator|.
name|println
argument_list|(
name|iter
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|stream
operator|.
name|println
argument_list|(
literal|"*** END "
operator|+
name|label
operator|+
literal|" ***"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convinience method for logging an array.  Wraps the array in an iterator and delegates    *    * @see dumpIterator(String,Iterator,PrintStream)    */
DECL|method|dumpArray
specifier|public
specifier|static
name|void
name|dumpArray
parameter_list|(
name|String
name|label
parameter_list|,
name|Object
index|[]
name|objs
parameter_list|,
name|PrintStream
name|stream
parameter_list|)
block|{
name|Iterator
name|iter
init|=
operator|(
literal|null
operator|==
name|objs
operator|)
condition|?
literal|null
else|:
name|Arrays
operator|.
name|asList
argument_list|(
name|objs
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|dumpIterator
argument_list|(
name|label
argument_list|,
name|iter
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a {@link Random} instance for generating random numbers during the test.    * The random seed is logged during test execution and printed to System.out on any failure    * for reproducing the test using {@link #newRandom(long)} with the recorded seed    * .    */
DECL|method|newRandom
specifier|public
name|Random
name|newRandom
parameter_list|()
block|{
if|if
condition|(
name|seed
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"please call LuceneTestCaseJ4.newRandom only once per test"
argument_list|)
throw|;
block|}
return|return
name|newRandom
argument_list|(
name|seedRnd
operator|.
name|nextLong
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns a {@link Random} instance for generating random numbers during the test.    * If an error occurs in the test that is not reproducible, you can use this method to    * initialize the number generator with the seed that was printed out during the failing test.    */
DECL|method|newRandom
specifier|public
name|Random
name|newRandom
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|seed
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"please call LuceneTestCaseJ4.newRandom only once per test"
argument_list|)
throw|;
block|}
name|this
operator|.
name|seed
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|seed
argument_list|)
expr_stmt|;
return|return
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|// We get here fro InterceptTestCaseEvents on the 'failed' event....
DECL|method|reportAdditionalFailureInfo
specifier|public
name|void
name|reportAdditionalFailureInfo
parameter_list|()
block|{
if|if
condition|(
name|seed
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: random seed of testcase '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' was: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
comment|// recorded seed
DECL|field|seed
specifier|protected
name|Long
name|seed
init|=
literal|null
decl_stmt|;
comment|// static members
DECL|field|seedRnd
specifier|private
specifier|static
specifier|final
name|Random
name|seedRnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
init|=
literal|""
decl_stmt|;
block|}
end_class

end_unit


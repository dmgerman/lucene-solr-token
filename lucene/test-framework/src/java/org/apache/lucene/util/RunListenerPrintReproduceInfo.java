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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|Failure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|RunListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|LifecycleScope
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedContext
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A suite listener printing a "reproduce string". This ensures test result  * events are always captured properly even if exceptions happen at  * initialization or suite/ hooks level.  */
end_comment

begin_class
DECL|class|RunListenerPrintReproduceInfo
specifier|public
specifier|final
class|class
name|RunListenerPrintReproduceInfo
extends|extends
name|RunListener
block|{
comment|/**    * A list of all test suite classes executed so far in this JVM (ehm,     * under this class's classloader).    */
DECL|field|testClassesRun
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|testClassesRun
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * The currently executing scope.    */
DECL|field|scope
specifier|private
name|LifecycleScope
name|scope
decl_stmt|;
comment|/** Current test failed. */
DECL|field|testFailed
specifier|private
name|boolean
name|testFailed
decl_stmt|;
comment|/** Suite-level code (initialization, rule, hook) failed. */
DECL|field|suiteFailed
specifier|private
name|boolean
name|suiteFailed
decl_stmt|;
comment|/** A marker to print full env. diagnostics after the suite. */
DECL|field|printDiagnosticsAfterClass
specifier|private
name|boolean
name|printDiagnosticsAfterClass
decl_stmt|;
annotation|@
name|Override
DECL|method|testRunStarted
specifier|public
name|void
name|testRunStarted
parameter_list|(
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
name|suiteFailed
operator|=
literal|false
expr_stmt|;
name|testFailed
operator|=
literal|false
expr_stmt|;
name|scope
operator|=
name|LifecycleScope
operator|.
name|SUITE
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|targetClass
init|=
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getTargetClass
argument_list|()
decl_stmt|;
name|testClassesRun
operator|.
name|add
argument_list|(
name|targetClass
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testStarted
specifier|public
name|void
name|testStarted
parameter_list|(
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|testFailed
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|LifecycleScope
operator|.
name|TEST
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testFailure
specifier|public
name|void
name|testFailure
parameter_list|(
name|Failure
name|failure
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|scope
operator|==
name|LifecycleScope
operator|.
name|TEST
condition|)
block|{
name|testFailed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|suiteFailed
operator|=
literal|true
expr_stmt|;
block|}
name|printDiagnosticsAfterClass
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testFinished
specifier|public
name|void
name|testFinished
parameter_list|(
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|testFailed
condition|)
block|{
name|reportAdditionalFailureInfo
argument_list|(
name|stripTestNameAugmentations
argument_list|(
name|description
operator|.
name|getMethodName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|scope
operator|=
name|LifecycleScope
operator|.
name|SUITE
expr_stmt|;
name|testFailed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * The {@link Description} object in JUnit does not expose the actual test method,    * instead it has the concept of a unique "name" of a test. To run the same method (tests)    * repeatedly, randomizedtesting must make those "names" unique: it appends the current iteration    * and seeds to the test method's name. We strip this information here.       */
DECL|method|stripTestNameAugmentations
specifier|private
name|String
name|stripTestNameAugmentations
parameter_list|(
name|String
name|methodName
parameter_list|)
block|{
if|if
condition|(
name|methodName
operator|!=
literal|null
condition|)
block|{
name|methodName
operator|=
name|methodName
operator|.
name|replaceAll
argument_list|(
literal|"\\s*\\{.+?\\}"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|methodName
return|;
block|}
annotation|@
name|Override
DECL|method|testRunFinished
specifier|public
name|void
name|testRunFinished
parameter_list|(
name|Result
name|result
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|printDiagnosticsAfterClass
operator|||
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|RunListenerPrintReproduceInfo
operator|.
name|printDebuggingInformation
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|suiteFailed
condition|)
block|{
name|reportAdditionalFailureInfo
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** print some useful debugging information about the environment */
DECL|method|printDebuggingInformation
specifier|private
specifier|static
name|void
name|printDebuggingInformation
parameter_list|()
block|{
if|if
condition|(
name|classEnvRule
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: test params are: codec="
operator|+
name|classEnvRule
operator|.
name|codec
operator|+
literal|", sim="
operator|+
name|classEnvRule
operator|.
name|similarity
operator|+
literal|", locale="
operator|+
name|classEnvRule
operator|.
name|locale
operator|+
literal|", timezone="
operator|+
operator|(
name|classEnvRule
operator|.
name|timeZone
operator|==
literal|null
condition|?
literal|"(null)"
else|:
name|classEnvRule
operator|.
name|timeZone
operator|.
name|getID
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|+
literal|" "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.version"
argument_list|)
operator|+
literal|" "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.arch"
argument_list|)
operator|+
literal|"/"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
operator|+
literal|" "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|+
literal|" "
operator|+
operator|(
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
literal|"(64-bit)"
else|:
literal|"(32-bit)"
operator|)
operator|+
literal|"/"
operator|+
literal|"cpus="
operator|+
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|+
literal|","
operator|+
literal|"threads="
operator|+
name|Thread
operator|.
name|activeCount
argument_list|()
operator|+
literal|","
operator|+
literal|"free="
operator|+
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
operator|+
literal|","
operator|+
literal|"total="
operator|+
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: All tests run in this JVM: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|testClassesRun
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|reportAdditionalFailureInfo
specifier|private
name|void
name|reportAdditionalFailureInfo
parameter_list|(
specifier|final
name|String
name|testName
parameter_list|)
block|{
if|if
condition|(
name|TEST_LINE_DOCS_FILE
operator|.
name|endsWith
argument_list|(
name|JENKINS_LARGE_LINE_DOCS_FILE
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: download the large Jenkins line-docs file by running "
operator|+
literal|"'ant get-jenkins-line-docs' in the lucene directory."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"NOTE: reproduce with: ant test "
argument_list|)
expr_stmt|;
comment|// Test case, method, seed.
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"testcase"
argument_list|,
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getTargetClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.method"
argument_list|,
name|testName
argument_list|)
expr_stmt|;
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.seed"
argument_list|,
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getRunnerSeedAsString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test groups and multipliers.
if|if
condition|(
name|RANDOM_MULTIPLIER
operator|>
literal|1
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.multiplier"
argument_list|,
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
if|if
condition|(
name|TEST_NIGHTLY
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
name|SYSPROP_NIGHTLY
argument_list|,
name|TEST_NIGHTLY
argument_list|)
expr_stmt|;
if|if
condition|(
name|TEST_WEEKLY
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
name|SYSPROP_WEEKLY
argument_list|,
name|TEST_WEEKLY
argument_list|)
expr_stmt|;
if|if
condition|(
name|TEST_SLOW
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
name|SYSPROP_SLOW
argument_list|,
name|TEST_SLOW
argument_list|)
expr_stmt|;
if|if
condition|(
name|TEST_AWAITSFIX
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
name|SYSPROP_AWAITSFIX
argument_list|,
name|TEST_AWAITSFIX
argument_list|)
expr_stmt|;
comment|// Codec, postings, directories.
if|if
condition|(
operator|!
name|TEST_CODEC
operator|.
name|equals
argument_list|(
literal|"random"
argument_list|)
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.codec"
argument_list|,
name|TEST_CODEC
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|TEST_POSTINGSFORMAT
operator|.
name|equals
argument_list|(
literal|"random"
argument_list|)
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.postingsformat"
argument_list|,
name|TEST_POSTINGSFORMAT
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|TEST_DOCVALUESFORMAT
operator|.
name|equals
argument_list|(
literal|"random"
argument_list|)
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.docvaluesformat"
argument_list|,
name|TEST_DOCVALUESFORMAT
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|TEST_DIRECTORY
operator|.
name|equals
argument_list|(
literal|"random"
argument_list|)
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.directory"
argument_list|,
name|TEST_DIRECTORY
argument_list|)
expr_stmt|;
comment|// Environment.
if|if
condition|(
operator|!
name|TEST_LINE_DOCS_FILE
operator|.
name|equals
argument_list|(
name|DEFAULT_LINE_DOCS_FILE
argument_list|)
condition|)
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.linedocsfile"
argument_list|,
name|TEST_LINE_DOCS_FILE
argument_list|)
expr_stmt|;
if|if
condition|(
name|classEnvRule
operator|!=
literal|null
condition|)
block|{
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.locale"
argument_list|,
name|classEnvRule
operator|.
name|locale
argument_list|)
expr_stmt|;
if|if
condition|(
name|classEnvRule
operator|.
name|timeZone
operator|!=
literal|null
condition|)
block|{
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.timezone"
argument_list|,
name|classEnvRule
operator|.
name|timeZone
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LuceneTestCase
operator|.
name|assertsAreEnabled
condition|)
block|{
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.asserts"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.asserts"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
name|addVmOpt
argument_list|(
name|b
argument_list|,
literal|"tests.file.encoding"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.encoding"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Append a VM option (-Dkey=value) to a {@link StringBuilder}. Add quotes if     * spaces or other funky characters are detected.    */
DECL|method|addVmOpt
specifier|static
name|void
name|addVmOpt
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return;
name|b
operator|.
name|append
argument_list|(
literal|" -D"
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|String
name|v
init|=
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Add simplistic quoting. This varies a lot from system to system and between
comment|// shells... ANT should have some code for doing it properly.
if|if
condition|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\\s=']"
argument_list|)
operator|.
name|matcher
argument_list|(
name|v
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|v
operator|=
literal|'"'
operator|+
name|v
operator|+
literal|'"'
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


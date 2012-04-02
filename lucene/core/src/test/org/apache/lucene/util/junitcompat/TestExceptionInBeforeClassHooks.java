begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
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
name|regex
operator|.
name|Matcher
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|JUnitCore
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

begin_class
DECL|class|TestExceptionInBeforeClassHooks
specifier|public
class|class
name|TestExceptionInBeforeClassHooks
extends|extends
name|WithNestedTests
block|{
DECL|method|TestExceptionInBeforeClassHooks
specifier|public
name|TestExceptionInBeforeClassHooks
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|Nested1
specifier|public
specifier|static
class|class
name|Nested1
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foobar"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{}
block|}
DECL|class|Nested2
specifier|public
specifier|static
class|class
name|Nested2
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
DECL|method|test1
specifier|public
name|void
name|test1
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foobar1"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|test2
specifier|public
name|void
name|test2
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foobar2"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|test3
specifier|public
name|void
name|test3
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foobar3"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Nested3
specifier|public
specifier|static
class|class
name|Nested3
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
annotation|@
name|Before
DECL|method|runBeforeTest
specifier|public
name|void
name|runBeforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foobar"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|test1
specifier|public
name|void
name|test1
parameter_list|()
throws|throws
name|Exception
block|{     }
block|}
annotation|@
name|Test
DECL|method|testExceptionInBeforeClassFailsTheTest
specifier|public
name|void
name|testExceptionInBeforeClassFailsTheTest
parameter_list|()
block|{
name|Result
name|runClasses
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested1
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|runClasses
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|runClasses
operator|.
name|getRunCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runClasses
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTrace
argument_list|()
operator|.
name|contains
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExceptionWithinTestFailsTheTest
specifier|public
name|void
name|testExceptionWithinTestFailsTheTest
parameter_list|()
block|{
name|Result
name|runClasses
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested2
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|runClasses
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|runClasses
operator|.
name|getRunCount
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|foobars
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Failure
name|f
range|:
name|runClasses
operator|.
name|getFailures
argument_list|()
control|)
block|{
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"foobar[0-9]+"
argument_list|)
operator|.
name|matcher
argument_list|(
name|f
operator|.
name|getTrace
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|foobars
operator|.
name|add
argument_list|(
name|m
operator|.
name|group
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|foobars
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[foobar1, foobar2, foobar3]"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|foobars
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExceptionWithinBefore
specifier|public
name|void
name|testExceptionWithinBefore
parameter_list|()
block|{
name|Result
name|runClasses
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested3
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|runClasses
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|runClasses
operator|.
name|getRunCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runClasses
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTrace
argument_list|()
operator|.
name|contains
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


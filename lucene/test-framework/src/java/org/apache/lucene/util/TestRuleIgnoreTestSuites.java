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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
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
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This rule will cause the suite to be assumption-ignored if   * the test class implements a given marker interface and a special  * property is not set.  *   *<p>This is a workaround for problems with certain JUnit containers (IntelliJ)  * which automatically discover test suites and attempt to run nested classes  * that we use for testing the test framework itself.  */
end_comment

begin_class
DECL|class|TestRuleIgnoreTestSuites
specifier|public
specifier|final
class|class
name|TestRuleIgnoreTestSuites
implements|implements
name|TestRule
block|{
comment|/**     * Marker interface for nested suites that should be ignored    * if executed in stand-alone mode.    */
DECL|interface|NestedTestSuite
specifier|public
specifier|static
interface|interface
name|NestedTestSuite
block|{}
comment|/**    * A boolean system property indicating nested suites should be executed    * normally.    */
DECL|field|PROPERTY_RUN_NESTED
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_RUN_NESTED
init|=
literal|"tests.runnested"
decl_stmt|;
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|s
parameter_list|,
specifier|final
name|Description
name|d
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|NestedTestSuite
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|d
operator|.
name|getTestClass
argument_list|()
argument_list|)
condition|)
block|{
name|LuceneTestCase
operator|.
name|assumeTrue
argument_list|(
literal|"Nested suite class ignored (started as stand-alone)."
argument_list|,
name|isRunningNested
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**    * Check if a suite class is running as a nested test.    */
DECL|method|isRunningNested
specifier|public
specifier|static
name|boolean
name|isRunningNested
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|PROPERTY_RUN_NESTED
argument_list|)
return|;
block|}
block|}
end_class

end_unit


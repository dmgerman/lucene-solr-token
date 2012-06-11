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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|AfterClass
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
name|RuleChain
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
name|MultipleFailureException
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
comment|/**  * A {@link TestRule} that guarantees the execution of {@link #after} even  * if an exception has been thrown from delegate {@link Statement}. This is much  * like {@link AfterClass} or {@link After} annotations but can be used with  * {@link RuleChain} to guarantee the order of execution.  */
end_comment

begin_class
DECL|class|AbstractBeforeAfterRule
specifier|abstract
class|class
name|AbstractBeforeAfterRule
implements|implements
name|TestRule
block|{
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
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|before
argument_list|()
expr_stmt|;
name|s
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|after
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|MultipleFailureException
operator|.
name|assertEmpty
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|before
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{}
DECL|method|after
specifier|protected
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{}
block|}
end_class

end_unit


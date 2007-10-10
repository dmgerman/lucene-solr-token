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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/** Base class for all Lucene unit tests.  Currently the  *  only added functionality over JUnit's TestCase is  *  asserting that no unhandled exceptions occurred in  *  threads launched by ConcurrentMergeScheduler.  If you  *  override either<code>setUp()</code> or  *<code>tearDown()</code> in your unit test, make sure you  *  call<code>super.setUp()</code> and  *<code>super.tearDown()</code>.  */
end_comment

begin_class
DECL|class|LuceneTestCase
specifier|public
class|class
name|LuceneTestCase
extends|extends
name|TestCase
block|{
DECL|method|LuceneTestCase
specifier|public
name|LuceneTestCase
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|LuceneTestCase
specifier|public
name|LuceneTestCase
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
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
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|ConcurrentMergeScheduler
operator|.
name|anyUnhandledExceptions
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"ConcurrentMergeScheduler hit unhandled exceptions"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


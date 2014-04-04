begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|index
operator|.
name|AtomicReaderContext
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
name|Collector
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
name|Scorer
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
name|LuceneTestCase
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

begin_class
DECL|class|MultiCollectorTest
specifier|public
class|class
name|MultiCollectorTest
extends|extends
name|LuceneTestCase
block|{
DECL|class|DummyCollector
specifier|private
specifier|static
class|class
name|DummyCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|acceptsDocsOutOfOrderCalled
name|boolean
name|acceptsDocsOutOfOrderCalled
init|=
literal|false
decl_stmt|;
DECL|field|collectCalled
name|boolean
name|collectCalled
init|=
literal|false
decl_stmt|;
DECL|field|setNextReaderCalled
name|boolean
name|setNextReaderCalled
init|=
literal|false
decl_stmt|;
DECL|field|setScorerCalled
name|boolean
name|setScorerCalled
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
name|acceptsDocsOutOfOrderCalled
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|collectCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|setNextReaderCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|setScorerCalled
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNullCollectors
specifier|public
name|void
name|testNullCollectors
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Tests that the collector rejects all null collectors.
try|try
block|{
name|MultiCollector
operator|.
name|wrap
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"only null collectors should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// Tests that the collector handles some null collectors well. If it
comment|// doesn't, an NPE would be thrown.
name|Collector
name|c
init|=
name|MultiCollector
operator|.
name|wrap
argument_list|(
operator|new
name|DummyCollector
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|DummyCollector
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|c
operator|instanceof
name|MultiCollector
argument_list|)
expr_stmt|;
specifier|final
name|LeafCollector
name|ac
init|=
name|c
operator|.
name|getLeafCollector
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ac
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
argument_list|)
expr_stmt|;
name|ac
operator|.
name|collect
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|c
operator|.
name|getLeafCollector
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|c
operator|.
name|getLeafCollector
argument_list|(
literal|null
argument_list|)
operator|.
name|setScorer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleCollector
specifier|public
name|void
name|testSingleCollector
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Tests that if a single Collector is input, it is returned (and not MultiCollector).
name|DummyCollector
name|dc
init|=
operator|new
name|DummyCollector
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|dc
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|dc
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|dc
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|dc
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCollector
specifier|public
name|void
name|testCollector
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Tests that the collector delegates calls to input collectors properly.
comment|// Tests that the collector handles some null collectors well. If it
comment|// doesn't, an NPE would be thrown.
name|DummyCollector
index|[]
name|dcs
init|=
operator|new
name|DummyCollector
index|[]
block|{
operator|new
name|DummyCollector
argument_list|()
block|,
operator|new
name|DummyCollector
argument_list|()
block|}
decl_stmt|;
name|Collector
name|c
init|=
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|dcs
argument_list|)
decl_stmt|;
name|LeafCollector
name|ac
init|=
name|c
operator|.
name|getLeafCollector
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ac
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
argument_list|)
expr_stmt|;
name|ac
operator|.
name|collect
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ac
operator|=
name|c
operator|.
name|getLeafCollector
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ac
operator|.
name|setScorer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|DummyCollector
name|dc
range|:
name|dcs
control|)
block|{
name|assertTrue
argument_list|(
name|dc
operator|.
name|acceptsDocsOutOfOrderCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dc
operator|.
name|collectCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dc
operator|.
name|setNextReaderCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dc
operator|.
name|setScorerCalled
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


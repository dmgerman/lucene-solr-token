begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|Random
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
name|LeafReaderContext
import|;
end_import

begin_comment
comment|/**  * A collector that asserts that it is used correctly.  */
end_comment

begin_class
DECL|class|AssertingCollector
class|class
name|AssertingCollector
extends|extends
name|FilterCollector
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Wrap the given collector in order to add assertions. */
DECL|method|wrap
specifier|public
specifier|static
name|Collector
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|Collector
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|instanceof
name|AssertingCollector
condition|)
block|{
return|return
name|in
return|;
block|}
return|return
operator|new
name|AssertingCollector
argument_list|(
name|random
argument_list|,
name|in
argument_list|)
return|;
block|}
DECL|method|AssertingCollector
specifier|private
name|AssertingCollector
parameter_list|(
name|Random
name|random
parameter_list|,
name|Collector
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafCollector
name|in
init|=
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|int
name|docBase
init|=
name|context
operator|.
name|docBase
decl_stmt|;
return|return
operator|new
name|AssertingLeafCollector
argument_list|(
name|random
argument_list|,
name|in
argument_list|,
literal|0
argument_list|,
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
block|{
annotation|@
name|Override
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
comment|// check that documents are scored in order globally,
comment|// not only per segment
assert|assert
name|docBase
operator|+
name|doc
operator|>=
name|maxDoc
operator|:
literal|"collection is not in order: current doc="
operator|+
operator|(
name|docBase
operator|+
name|doc
operator|)
operator|+
literal|" while "
operator|+
name|maxDoc
operator|+
literal|" has already been collected"
assert|;
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|maxDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit


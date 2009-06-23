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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|IndexReader
import|;
end_import

begin_comment
comment|/**   * Constrains search results to only match those which also match a provided  * query.    *  *<p> This could be used, for example, with a {@link RangeQuery} on a suitably  * formatted date field to implement date filtering.  One could re-use a single  * QueryFilter that matches, e.g., only documents modified within the last  * week.  The QueryFilter and RangeQuery would only need to be reconstructed  * once per day.  *  * @version $Id:$  */
end_comment

begin_class
DECL|class|QueryWrapperFilter
specifier|public
class|class
name|QueryWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
comment|/** Constructs a filter which only matches documents matching    *<code>query</code>.    */
DECL|method|QueryWrapperFilter
specifier|public
name|QueryWrapperFilter
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
comment|/**    * @deprecated Use {@link #getDocIdSet(IndexReader)} instead.    */
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|Collector
argument_list|()
block|{
specifier|private
name|int
name|base
init|=
literal|0
decl_stmt|;
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
comment|// score is not needed by this collector
block|}
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
operator|+
name|base
argument_list|)
expr_stmt|;
comment|// set bit for hit
block|}
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
name|base
operator|=
name|docBase
expr_stmt|;
block|}
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|bits
return|;
block|}
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|QueryWeight
name|weight
init|=
name|query
operator|.
name|queryWeight
argument_list|(
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"QueryWrapperFilter("
operator|+
name|query
operator|+
literal|")"
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|QueryWrapperFilter
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|QueryWrapperFilter
operator|)
name|o
operator|)
operator|.
name|query
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|query
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x923F64B9
return|;
block|}
block|}
end_class

end_unit


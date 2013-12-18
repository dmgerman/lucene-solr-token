begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|document
operator|.
name|NumericDocValuesField
import|;
end_import

begin_comment
comment|// javadocs
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|Filter
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
name|Bits
import|;
end_import

begin_comment
comment|/** Represents a range over long values. */
end_comment

begin_class
DECL|class|LongRange
specifier|public
specifier|final
class|class
name|LongRange
extends|extends
name|Range
block|{
DECL|field|minIncl
specifier|final
name|long
name|minIncl
decl_stmt|;
DECL|field|maxIncl
specifier|final
name|long
name|maxIncl
decl_stmt|;
DECL|field|min
specifier|public
specifier|final
name|long
name|min
decl_stmt|;
DECL|field|max
specifier|public
specifier|final
name|long
name|max
decl_stmt|;
DECL|field|minInclusive
specifier|public
specifier|final
name|boolean
name|minInclusive
decl_stmt|;
DECL|field|maxInclusive
specifier|public
specifier|final
name|boolean
name|maxInclusive
decl_stmt|;
comment|// TODO: can we require fewer args? (same for
comment|// Double/FloatRange too)
comment|/** Create a LongRange. */
DECL|method|LongRange
specifier|public
name|LongRange
parameter_list|(
name|String
name|label
parameter_list|,
name|long
name|minIn
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|long
name|maxIn
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|super
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|minIn
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|maxIn
expr_stmt|;
name|this
operator|.
name|minInclusive
operator|=
name|minInclusive
expr_stmt|;
name|this
operator|.
name|maxInclusive
operator|=
name|maxInclusive
expr_stmt|;
if|if
condition|(
operator|!
name|minInclusive
condition|)
block|{
if|if
condition|(
name|minIn
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|minIn
operator|++
expr_stmt|;
block|}
else|else
block|{
name|failNoMatch
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|maxInclusive
condition|)
block|{
if|if
condition|(
name|maxIn
operator|!=
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
name|maxIn
operator|--
expr_stmt|;
block|}
else|else
block|{
name|failNoMatch
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|minIn
operator|>
name|maxIn
condition|)
block|{
name|failNoMatch
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|minIncl
operator|=
name|minIn
expr_stmt|;
name|this
operator|.
name|maxIncl
operator|=
name|maxIn
expr_stmt|;
block|}
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|value
operator|>=
name|minIncl
operator|&&
name|value
operator|<=
name|maxIncl
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"LongRange("
operator|+
name|minIncl
operator|+
literal|" to "
operator|+
name|maxIncl
operator|+
literal|")"
return|;
block|}
comment|/** Returns a new {@link Filter} accepting only documents    *  in this range.  Note that this filter is not    *  efficient: it's a linear scan of all docs, testing    *  each value.  If the {@link ValueSource} is static,    *  e.g. an indexed numeric field, then it's more    *  efficient to use {@link NumericRangeFilter}. */
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|(
specifier|final
name|ValueSource
name|valueSource
parameter_list|)
block|{
return|return
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: this is just like ValueSourceScorer,
comment|// ValueSourceFilter (spatial),
comment|// ValueSourceRangeFilter (solr); also,
comment|// https://issues.apache.org/jira/browse/LUCENE-4251
specifier|final
name|FunctionValues
name|values
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|doc
operator|++
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|maxDoc
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
name|acceptDocs
operator|!=
literal|null
operator|&&
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
name|long
name|v
init|=
name|values
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|accept
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|target
operator|-
literal|1
expr_stmt|;
return|return
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
comment|// Since we do a linear scan over all
comment|// documents, our cost is O(maxDoc):
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit


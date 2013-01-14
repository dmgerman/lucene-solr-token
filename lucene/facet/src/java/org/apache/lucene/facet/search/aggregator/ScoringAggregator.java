begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.aggregator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|aggregator
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
name|util
operator|.
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An {@link Aggregator} which updates the weight of a category according to the  * scores of the documents it was found in.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|ScoringAggregator
specifier|public
class|class
name|ScoringAggregator
implements|implements
name|Aggregator
block|{
DECL|field|scoreArray
specifier|private
specifier|final
name|float
index|[]
name|scoreArray
decl_stmt|;
DECL|field|hashCode
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
DECL|method|ScoringAggregator
specifier|public
name|ScoringAggregator
parameter_list|(
name|float
index|[]
name|counterArray
parameter_list|)
block|{
name|this
operator|.
name|scoreArray
operator|=
name|counterArray
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|scoreArray
operator|==
literal|null
condition|?
literal|0
else|:
name|scoreArray
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|int
name|docID
parameter_list|,
name|float
name|score
parameter_list|,
name|IntsRef
name|ordinals
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|scoreArray
index|[
name|ordinals
operator|.
name|ints
index|[
name|i
index|]
index|]
operator|+=
name|score
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ScoringAggregator
name|that
init|=
operator|(
name|ScoringAggregator
operator|)
name|obj
decl_stmt|;
return|return
name|that
operator|.
name|scoreArray
operator|==
name|this
operator|.
name|scoreArray
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|boolean
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit


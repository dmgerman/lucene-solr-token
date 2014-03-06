begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
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
name|AtomicReader
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
name|FieldComparator
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
name|search
operator|.
name|Sort
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
name|SortField
import|;
end_import

begin_comment
comment|// nocommit: temporary class to engage the cutover!
end_comment

begin_class
DECL|class|SortSorter
specifier|public
class|class
name|SortSorter
extends|extends
name|Sorter
block|{
DECL|field|sort
specifier|final
name|Sort
name|sort
decl_stmt|;
DECL|method|SortSorter
specifier|public
name|SortSorter
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sort
specifier|public
name|DocMap
name|sort
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SortField
name|fields
index|[]
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
specifier|final
name|int
name|reverseMul
index|[]
init|=
operator|new
name|int
index|[
name|fields
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|comparators
index|[]
init|=
operator|new
name|FieldComparator
index|[
name|fields
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|reverseMul
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|getComparator
argument_list|(
literal|2
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setNextReader
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setScorer
argument_list|(
name|FAKESCORER
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocComparator
name|comparator
init|=
operator|new
name|DocComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|docID1
parameter_list|,
name|int
name|docID2
parameter_list|)
block|{
try|try
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
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|docID1
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|copy
argument_list|(
literal|1
argument_list|,
name|docID2
argument_list|)
expr_stmt|;
name|int
name|comp
init|=
name|reverseMul
index|[
name|i
index|]
operator|*
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|comp
operator|!=
literal|0
condition|)
block|{
return|return
name|comp
return|;
block|}
block|}
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|docID1
argument_list|,
name|docID2
argument_list|)
return|;
comment|// docid order tiebreak
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
return|return
name|sort
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|comparator
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getID
specifier|public
name|String
name|getID
parameter_list|()
block|{
return|return
name|sort
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|FAKESCORER
specifier|static
specifier|final
name|Scorer
name|FAKESCORER
init|=
operator|new
name|Scorer
argument_list|(
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit


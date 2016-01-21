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
name|java
operator|.
name|util
operator|.
name|Objects
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
name|DocValues
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
name|DocValuesType
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
name|SortedNumericDocValues
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
name|SortedSetDocValues
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * A range query that works on top of the doc values APIs. Such queries are  * usually slow since they do not use an inverted index. However, in the  * dense case where most documents match this query, it<b>might</b> be as  * fast or faster than a regular {@link PointRangeQuery}.  *  *<p>  *<b>NOTE</b>: be very careful using this query: it is  * typically much slower than using {@code TermsQuery},  * but in certain specialized cases may be faster.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocValuesRangeQuery
specifier|public
specifier|final
class|class
name|DocValuesRangeQuery
extends|extends
name|Query
block|{
comment|/** Create a new numeric range query on a numeric doc-values field. The field    *  must has been indexed with either {@link DocValuesType#NUMERIC} or    *  {@link DocValuesType#SORTED_NUMERIC} doc values. */
DECL|method|newLongRange
specifier|public
specifier|static
name|Query
name|newLongRange
parameter_list|(
name|String
name|field
parameter_list|,
name|Long
name|lowerVal
parameter_list|,
name|Long
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
operator|new
name|DocValuesRangeQuery
argument_list|(
name|field
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
comment|/** Create a new numeric range query on a numeric doc-values field. The field    *  must has been indexed with {@link DocValuesType#SORTED} or    *  {@link DocValuesType#SORTED_SET} doc values. */
DECL|method|newBytesRefRange
specifier|public
specifier|static
name|Query
name|newBytesRefRange
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|lowerVal
parameter_list|,
name|BytesRef
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
operator|new
name|DocValuesRangeQuery
argument_list|(
name|field
argument_list|,
name|deepCopyOf
argument_list|(
name|lowerVal
argument_list|)
argument_list|,
name|deepCopyOf
argument_list|(
name|upperVal
argument_list|)
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
DECL|method|deepCopyOf
specifier|private
specifier|static
name|BytesRef
name|deepCopyOf
parameter_list|(
name|BytesRef
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|b
argument_list|)
return|;
block|}
block|}
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|lowerVal
DECL|field|upperVal
specifier|private
specifier|final
name|Object
name|lowerVal
decl_stmt|,
name|upperVal
decl_stmt|;
DECL|field|includeLower
DECL|field|includeUpper
specifier|private
specifier|final
name|boolean
name|includeLower
decl_stmt|,
name|includeUpper
decl_stmt|;
DECL|method|DocValuesRangeQuery
specifier|private
name|DocValuesRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Object
name|lowerVal
parameter_list|,
name|Object
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|lowerVal
operator|=
name|lowerVal
expr_stmt|;
name|this
operator|.
name|upperVal
operator|=
name|upperVal
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
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
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|DocValuesRangeQuery
name|that
init|=
operator|(
name|DocValuesRangeQuery
operator|)
name|obj
decl_stmt|;
return|return
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|lowerVal
argument_list|,
name|that
operator|.
name|lowerVal
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|upperVal
argument_list|,
name|that
operator|.
name|upperVal
argument_list|)
operator|&&
name|includeLower
operator|==
name|that
operator|.
name|includeLower
operator|&&
name|includeUpper
operator|==
name|that
operator|.
name|includeUpper
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
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
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|Objects
operator|.
name|hash
argument_list|(
name|field
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|includeLower
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|lowerVal
operator|==
literal|null
condition|?
literal|"*"
else|:
name|lowerVal
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|upperVal
operator|==
literal|null
condition|?
literal|"*"
else|:
name|upperVal
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|includeUpper
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lowerVal
operator|==
literal|null
operator|&&
name|upperVal
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|FieldValueQuery
argument_list|(
name|field
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lowerVal
operator|==
literal|null
operator|&&
name|upperVal
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Both min and max values cannot be null, call rewrite first"
argument_list|)
throw|;
block|}
return|return
operator|new
name|RandomAccessWeight
argument_list|(
name|DocValuesRangeQuery
operator|.
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Bits
name|getMatchingDocs
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lowerVal
operator|instanceof
name|Long
operator|||
name|upperVal
operator|instanceof
name|Long
condition|)
block|{
specifier|final
name|SortedNumericDocValues
name|values
init|=
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|long
name|min
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|min
operator|=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeLower
condition|)
block|{
name|min
operator|=
operator|(
name|long
operator|)
name|lowerVal
expr_stmt|;
block|}
else|else
block|{
name|min
operator|=
literal|1
operator|+
operator|(
name|long
operator|)
name|lowerVal
expr_stmt|;
block|}
specifier|final
name|long
name|max
decl_stmt|;
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|max
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeUpper
condition|)
block|{
name|max
operator|=
operator|(
name|long
operator|)
name|upperVal
expr_stmt|;
block|}
else|else
block|{
name|max
operator|=
operator|-
literal|1
operator|+
operator|(
name|long
operator|)
name|upperVal
expr_stmt|;
block|}
if|if
condition|(
name|min
operator|>
name|max
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|values
operator|.
name|count
argument_list|()
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
name|count
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|value
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|>=
name|min
operator|&&
name|value
operator|<=
name|max
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
return|;
block|}
block|}
return|;
block|}
elseif|else
if|if
condition|(
name|lowerVal
operator|instanceof
name|BytesRef
operator|||
name|upperVal
operator|instanceof
name|BytesRef
condition|)
block|{
specifier|final
name|SortedSetDocValues
name|values
init|=
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|long
name|minOrd
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|minOrd
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
operator|(
name|BytesRef
operator|)
name|lowerVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|minOrd
operator|=
operator|-
literal|1
operator|-
name|ord
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeLower
condition|)
block|{
name|minOrd
operator|=
name|ord
expr_stmt|;
block|}
else|else
block|{
name|minOrd
operator|=
name|ord
operator|+
literal|1
expr_stmt|;
block|}
block|}
specifier|final
name|long
name|maxOrd
decl_stmt|;
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|maxOrd
operator|=
name|values
operator|.
name|getValueCount
argument_list|()
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
operator|(
name|BytesRef
operator|)
name|upperVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|maxOrd
operator|=
operator|-
literal|2
operator|-
name|ord
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeUpper
condition|)
block|{
name|maxOrd
operator|=
name|ord
expr_stmt|;
block|}
else|else
block|{
name|maxOrd
operator|=
name|ord
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|minOrd
operator|>
name|maxOrd
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|ord
init|=
name|values
operator|.
name|nextOrd
argument_list|()
init|;
name|ord
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|;
name|ord
operator|=
name|values
operator|.
name|nextOrd
argument_list|()
control|)
block|{
if|if
condition|(
name|ord
operator|>=
name|minOrd
operator|&&
name|ord
operator|<=
name|maxOrd
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
return|;
block|}
block|}
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit


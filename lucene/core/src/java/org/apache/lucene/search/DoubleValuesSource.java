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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|DoubleToLongFunction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|LongToDoubleFunction
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
name|NumericDocValues
import|;
end_import

begin_comment
comment|/**  * Base class for producing {@link DoubleValues}  *  * To obtain a {@link DoubleValues} object for a leaf reader, clients should  * call {@link #getValues(LeafReaderContext, DoubleValues)}.  *  * DoubleValuesSource objects for NumericDocValues fields can be obtained by calling  * {@link #fromDoubleField(String)}, {@link #fromFloatField(String)}, {@link #fromIntField(String)}  * or {@link #fromLongField(String)}, or from {@link #fromField(String, LongToDoubleFunction)} if  * special long-to-double encoding is required.  *  * Scores may be used as a source for value calculations by wrapping a {@link Scorer} using  * {@link #fromScorer(Scorer)} and passing the resulting DoubleValues to {@link #getValues(LeafReaderContext, DoubleValues)}.  * The scores can then be accessed using the {@link #SCORES} DoubleValuesSource.  */
end_comment

begin_class
DECL|class|DoubleValuesSource
specifier|public
specifier|abstract
class|class
name|DoubleValuesSource
block|{
comment|/**    * Returns a {@link DoubleValues} instance for the passed-in LeafReaderContext and scores    *    * If scores are not needed to calculate the values (ie {@link #needsScores() returns false}, callers    * may safely pass {@code null} for the {@code scores} parameter.    */
DECL|method|getValues
specifier|public
specifier|abstract
name|DoubleValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|DoubleValues
name|scores
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return true if document scores are needed to calculate values    */
DECL|method|needsScores
specifier|public
specifier|abstract
name|boolean
name|needsScores
parameter_list|()
function_decl|;
comment|/**    * Create a sort field based on the value of this producer    * @param reverse true if the sort should be decreasing    */
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|DoubleValuesSortField
argument_list|(
name|this
argument_list|,
name|reverse
argument_list|)
return|;
block|}
comment|/**    * Convert to a LongValuesSource by casting the double values to longs    */
DECL|method|toLongValuesSource
specifier|public
specifier|final
name|LongValuesSource
name|toLongValuesSource
parameter_list|()
block|{
return|return
operator|new
name|LongValuesSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LongValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|DoubleValues
name|scores
parameter_list|)
throws|throws
name|IOException
block|{
name|DoubleValues
name|in
init|=
name|DoubleValuesSource
operator|.
name|this
operator|.
name|getValues
argument_list|(
name|ctx
argument_list|,
name|scores
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|long
operator|)
name|in
operator|.
name|doubleValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|DoubleValuesSource
operator|.
name|this
operator|.
name|needsScores
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Creates a DoubleValuesSource that wraps a generic NumericDocValues field    *    * @param field the field to wrap, must have NumericDocValues    * @param decoder a function to convert the long-valued doc values to doubles    */
DECL|method|fromField
specifier|public
specifier|static
name|DoubleValuesSource
name|fromField
parameter_list|(
name|String
name|field
parameter_list|,
name|LongToDoubleFunction
name|decoder
parameter_list|)
block|{
return|return
operator|new
name|FieldValuesSource
argument_list|(
name|field
argument_list|,
name|decoder
argument_list|)
return|;
block|}
comment|/**    * Creates a DoubleValuesSource that wraps a double-valued field    */
DECL|method|fromDoubleField
specifier|public
specifier|static
name|DoubleValuesSource
name|fromDoubleField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|fromField
argument_list|(
name|field
argument_list|,
name|Double
operator|::
name|longBitsToDouble
argument_list|)
return|;
block|}
comment|/**    * Creates a DoubleValuesSource that wraps a float-valued field    */
DECL|method|fromFloatField
specifier|public
specifier|static
name|DoubleValuesSource
name|fromFloatField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|fromField
argument_list|(
name|field
argument_list|,
parameter_list|(
name|v
parameter_list|)
lambda|->
operator|(
name|double
operator|)
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|v
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a DoubleValuesSource that wraps a long-valued field    */
DECL|method|fromLongField
specifier|public
specifier|static
name|DoubleValuesSource
name|fromLongField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|fromField
argument_list|(
name|field
argument_list|,
parameter_list|(
name|v
parameter_list|)
lambda|->
operator|(
name|double
operator|)
name|v
argument_list|)
return|;
block|}
comment|/**    * Creates a DoubleValuesSource that wraps an int-valued field    */
DECL|method|fromIntField
specifier|public
specifier|static
name|DoubleValuesSource
name|fromIntField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|fromLongField
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/**    * A DoubleValuesSource that exposes a document's score    *    * If this source is used as part of a values calculation, then callers must not    * pass {@code null} as the {@link DoubleValues} parameter on {@link #getValues(LeafReaderContext, DoubleValues)}    */
DECL|field|SCORES
specifier|public
specifier|static
specifier|final
name|DoubleValuesSource
name|SCORES
init|=
operator|new
name|DoubleValuesSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DoubleValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|DoubleValues
name|scores
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|scores
operator|!=
literal|null
assert|;
return|return
name|scores
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Returns a DoubleValues instance that wraps scores returned by a Scorer    */
DECL|method|fromScorer
specifier|public
specifier|static
name|DoubleValues
name|fromScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
return|return
operator|new
name|DoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|double
name|doubleValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|scorer
operator|.
name|docID
argument_list|()
operator|==
name|doc
assert|;
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
DECL|class|FieldValuesSource
specifier|private
specifier|static
class|class
name|FieldValuesSource
extends|extends
name|DoubleValuesSource
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|decoder
specifier|final
name|LongToDoubleFunction
name|decoder
decl_stmt|;
DECL|method|FieldValuesSource
specifier|private
name|FieldValuesSource
parameter_list|(
name|String
name|field
parameter_list|,
name|LongToDoubleFunction
name|decoder
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|decoder
operator|=
name|decoder
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FieldValuesSource
name|that
init|=
operator|(
name|FieldValuesSource
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|field
argument_list|,
name|that
operator|.
name|field
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|decoder
argument_list|,
name|that
operator|.
name|decoder
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
name|Objects
operator|.
name|hash
argument_list|(
name|field
argument_list|,
name|decoder
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DoubleValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|DoubleValues
name|scores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NumericDocValues
name|values
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
name|toDoubleValues
argument_list|(
name|values
argument_list|,
name|decoder
operator|::
name|applyAsDouble
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|DoubleValuesSortField
specifier|private
specifier|static
class|class
name|DoubleValuesSortField
extends|extends
name|SortField
block|{
DECL|field|producer
specifier|final
name|DoubleValuesSource
name|producer
decl_stmt|;
DECL|method|DoubleValuesSortField
specifier|public
name|DoubleValuesSortField
parameter_list|(
name|DoubleValuesSource
name|producer
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|super
argument_list|(
name|producer
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|DoubleValuesComparatorSource
argument_list|(
name|producer
argument_list|)
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|producer
operator|.
name|needsScores
argument_list|()
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
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<"
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|reverse
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|"!"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|DoubleValuesHolder
specifier|private
specifier|static
class|class
name|DoubleValuesHolder
block|{
DECL|field|values
name|DoubleValues
name|values
decl_stmt|;
block|}
DECL|class|DoubleValuesComparatorSource
specifier|private
specifier|static
class|class
name|DoubleValuesComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|field|producer
specifier|private
specifier|final
name|DoubleValuesSource
name|producer
decl_stmt|;
DECL|method|DoubleValuesComparatorSource
specifier|public
name|DoubleValuesComparatorSource
parameter_list|(
name|DoubleValuesSource
name|producer
parameter_list|)
block|{
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
argument_list|<
name|Double
argument_list|>
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldComparator
operator|.
name|DoubleComparator
argument_list|(
name|numHits
argument_list|,
name|fieldname
argument_list|,
literal|0.0
argument_list|)
block|{
name|LeafReaderContext
name|ctx
decl_stmt|;
name|DoubleValuesHolder
name|holder
init|=
operator|new
name|DoubleValuesHolder
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ctx
operator|=
name|context
expr_stmt|;
return|return
name|asNumericDocValues
argument_list|(
name|holder
argument_list|,
name|Double
operator|::
name|doubleToLongBits
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|holder
operator|.
name|values
operator|=
name|producer
operator|.
name|getValues
argument_list|(
name|ctx
argument_list|,
name|fromScorer
argument_list|(
name|scorer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
DECL|method|toDoubleValues
specifier|private
specifier|static
name|DoubleValues
name|toDoubleValues
parameter_list|(
name|NumericDocValues
name|in
parameter_list|,
name|LongToDoubleFunction
name|map
parameter_list|)
block|{
return|return
operator|new
name|DoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|double
name|doubleValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|map
operator|.
name|applyAsDouble
argument_list|(
name|in
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advanceExact
argument_list|(
name|target
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|asNumericDocValues
specifier|private
specifier|static
name|NumericDocValues
name|asNumericDocValues
parameter_list|(
name|DoubleValuesHolder
name|in
parameter_list|,
name|DoubleToLongFunction
name|converter
parameter_list|)
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|converter
operator|.
name|applyAsLong
argument_list|(
name|in
operator|.
name|values
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|values
operator|.
name|advanceExact
argument_list|(
name|target
argument_list|)
return|;
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
return|;
block|}
block|}
end_class

end_unit

begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
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
name|valuesource
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
name|Map
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
name|NumericDocValues
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
name|docvalues
operator|.
name|LongDocValues
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
operator|.
name|Type
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
name|mutable
operator|.
name|MutableValue
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
name|mutable
operator|.
name|MutableValueLong
import|;
end_import

begin_comment
comment|/**  * Obtains long field values from {@link org.apache.lucene.index.LeafReader#getNumericDocValues} and makes those  * values available as other numeric types, casting as needed.  */
end_comment

begin_class
DECL|class|LongFieldSource
specifier|public
class|class
name|LongFieldSource
extends|extends
name|FieldCacheSource
block|{
DECL|method|LongFieldSource
specifier|public
name|LongFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"long("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
DECL|method|externalToLong
specifier|public
name|long
name|externalToLong
parameter_list|(
name|String
name|extVal
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|extVal
argument_list|)
return|;
block|}
DECL|method|longToObject
specifier|public
name|Object
name|longToObject
parameter_list|(
name|long
name|val
parameter_list|)
block|{
return|return
name|val
return|;
block|}
DECL|method|longToString
specifier|public
name|String
name|longToString
parameter_list|(
name|long
name|val
parameter_list|)
block|{
return|return
name|longToObject
argument_list|(
name|val
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|SortField
argument_list|(
name|field
argument_list|,
name|Type
operator|.
name|LONG
argument_list|,
name|reverse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NumericDocValues
name|arr
init|=
name|getNumericDocValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongDocValues
argument_list|(
name|this
argument_list|)
block|{
name|int
name|lastDocID
decl_stmt|;
specifier|private
name|long
name|getValueForDoc
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doc
operator|<
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docs were sent out-of-order: lastDocID="
operator|+
name|lastDocID
operator|+
literal|" vs docID="
operator|+
name|doc
argument_list|)
throw|;
block|}
name|lastDocID
operator|=
name|doc
expr_stmt|;
name|int
name|curDocID
init|=
name|arr
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>
name|curDocID
condition|)
block|{
name|curDocID
operator|=
name|arr
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|==
name|curDocID
condition|)
block|{
return|return
name|arr
operator|.
name|longValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getValueForDoc
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|getValueForDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|arr
operator|.
name|docID
argument_list|()
operator|==
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|value
init|=
name|getValueForDoc
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|arr
operator|.
name|docID
argument_list|()
operator|==
name|doc
condition|)
block|{
return|return
name|longToObject
argument_list|(
name|value
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|value
init|=
name|getValueForDoc
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|arr
operator|.
name|docID
argument_list|()
operator|==
name|doc
condition|)
block|{
return|return
name|longToString
argument_list|(
name|value
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|long
name|externalToLong
parameter_list|(
name|String
name|extVal
parameter_list|)
block|{
return|return
name|LongFieldSource
operator|.
name|this
operator|.
name|externalToLong
argument_list|(
name|extVal
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueLong
name|mval
init|=
name|newMutableValueLong
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|mval
operator|.
name|value
operator|=
name|getValueForDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
name|arr
operator|.
name|docID
argument_list|()
operator|==
name|doc
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|getNumericDocValues
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|method|newMutableValueLong
specifier|protected
name|MutableValueLong
name|newMutableValueLong
parameter_list|()
block|{
return|return
operator|new
name|MutableValueLong
argument_list|()
return|;
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
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|LongFieldSource
name|other
init|=
operator|(
name|LongFieldSource
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
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
name|int
name|h
init|=
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|+=
name|super
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit


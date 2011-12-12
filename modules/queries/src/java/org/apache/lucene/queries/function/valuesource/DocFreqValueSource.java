begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|Term
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|DoubleDocValues
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
name|IntDocValues
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
name|IndexSearcher
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

begin_class
DECL|class|ConstIntDocValues
class|class
name|ConstIntDocValues
extends|extends
name|IntDocValues
block|{
DECL|field|ival
specifier|final
name|int
name|ival
decl_stmt|;
DECL|field|fval
specifier|final
name|float
name|fval
decl_stmt|;
DECL|field|dval
specifier|final
name|double
name|dval
decl_stmt|;
DECL|field|lval
specifier|final
name|long
name|lval
decl_stmt|;
DECL|field|sval
specifier|final
name|String
name|sval
decl_stmt|;
DECL|field|parent
specifier|final
name|ValueSource
name|parent
decl_stmt|;
DECL|method|ConstIntDocValues
name|ConstIntDocValues
parameter_list|(
name|int
name|val
parameter_list|,
name|ValueSource
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|ival
operator|=
name|val
expr_stmt|;
name|fval
operator|=
name|val
expr_stmt|;
name|dval
operator|=
name|val
expr_stmt|;
name|lval
operator|=
name|val
expr_stmt|;
name|sval
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|floatVal
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|fval
return|;
block|}
annotation|@
name|Override
DECL|method|intVal
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ival
return|;
block|}
annotation|@
name|Override
DECL|method|longVal
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|lval
return|;
block|}
annotation|@
name|Override
DECL|method|doubleVal
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|dval
return|;
block|}
annotation|@
name|Override
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|sval
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|parent
operator|.
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|sval
return|;
block|}
block|}
end_class

begin_class
DECL|class|ConstDoubleDocValues
class|class
name|ConstDoubleDocValues
extends|extends
name|DoubleDocValues
block|{
DECL|field|ival
specifier|final
name|int
name|ival
decl_stmt|;
DECL|field|fval
specifier|final
name|float
name|fval
decl_stmt|;
DECL|field|dval
specifier|final
name|double
name|dval
decl_stmt|;
DECL|field|lval
specifier|final
name|long
name|lval
decl_stmt|;
DECL|field|sval
specifier|final
name|String
name|sval
decl_stmt|;
DECL|field|parent
specifier|final
name|ValueSource
name|parent
decl_stmt|;
DECL|method|ConstDoubleDocValues
name|ConstDoubleDocValues
parameter_list|(
name|double
name|val
parameter_list|,
name|ValueSource
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|ival
operator|=
operator|(
name|int
operator|)
name|val
expr_stmt|;
name|fval
operator|=
operator|(
name|float
operator|)
name|val
expr_stmt|;
name|dval
operator|=
name|val
expr_stmt|;
name|lval
operator|=
operator|(
name|long
operator|)
name|val
expr_stmt|;
name|sval
operator|=
name|Double
operator|.
name|toString
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|floatVal
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|fval
return|;
block|}
annotation|@
name|Override
DECL|method|intVal
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ival
return|;
block|}
annotation|@
name|Override
DECL|method|longVal
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|lval
return|;
block|}
annotation|@
name|Override
DECL|method|doubleVal
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|dval
return|;
block|}
annotation|@
name|Override
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|sval
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|parent
operator|.
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|sval
return|;
block|}
block|}
end_class

begin_comment
comment|/**  *<code>DocFreqValueSource</code> returns the number of documents containing the term.  * @lucene.internal  */
end_comment

begin_class
DECL|class|DocFreqValueSource
specifier|public
class|class
name|DocFreqValueSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
DECL|field|indexedField
specifier|protected
name|String
name|indexedField
decl_stmt|;
DECL|field|val
specifier|protected
name|String
name|val
decl_stmt|;
DECL|field|indexedBytes
specifier|protected
name|BytesRef
name|indexedBytes
decl_stmt|;
DECL|method|DocFreqValueSource
specifier|public
name|DocFreqValueSource
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|val
parameter_list|,
name|String
name|indexedField
parameter_list|,
name|BytesRef
name|indexedBytes
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
name|val
operator|=
name|val
expr_stmt|;
name|this
operator|.
name|indexedField
operator|=
name|indexedField
expr_stmt|;
name|this
operator|.
name|indexedBytes
operator|=
name|indexedBytes
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"docfreq"
return|;
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
name|name
argument_list|()
operator|+
literal|'('
operator|+
name|field
operator|+
literal|','
operator|+
name|val
operator|+
literal|')'
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
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
operator|(
name|IndexSearcher
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
decl_stmt|;
name|int
name|docfreq
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|indexedField
argument_list|,
name|indexedBytes
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstIntDocValues
argument_list|(
name|docfreq
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|context
operator|.
name|put
argument_list|(
literal|"searcher"
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
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
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|indexedField
operator|.
name|hashCode
argument_list|()
operator|*
literal|29
operator|+
name|indexedBytes
operator|.
name|hashCode
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
name|this
operator|.
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
name|DocFreqValueSource
name|other
init|=
operator|(
name|DocFreqValueSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|indexedField
operator|.
name|equals
argument_list|(
name|other
operator|.
name|indexedField
argument_list|)
operator|&&
name|this
operator|.
name|indexedBytes
operator|.
name|equals
argument_list|(
name|other
operator|.
name|indexedBytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit


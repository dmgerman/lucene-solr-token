begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|Searcher
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

begin_comment
comment|/**  *<code>ReciprocalFloatFunction</code> implements a reciprocal function f(x) = a/(mx+b), based on  * the float value of a field or function as exported by {@link org.apache.solr.search.function.ValueSource}.  *<br>  *  * When a and b are equal, and x>=0, this function has a maximum value of 1 that drops as x increases.  * Increasing the value of a and b together results in a movement of the entire function to a flatter part of the curve.  *<p>These properties make this an idea function for boosting more recent documents.  *<p>Example:<code>  recip(ms(NOW,mydatefield),3.16e-11,1,1)</code>  *<p>A multiplier of 3.16e-11 changes the units from milliseconds to years (since there are about 3.16e10 milliseconds  * per year).  Thus, a very recent date will yield a value close to 1/(0+1) or 1,  * a date a year in the past will get a multiplier of about 1/(1+1) or 1/2,  * and date two years old will yield 1/(2+1) or 1/3.  *  * @see FunctionQuery  *  * @version $Id$  */
end_comment

begin_class
DECL|class|ReciprocalFloatFunction
specifier|public
class|class
name|ReciprocalFloatFunction
extends|extends
name|ValueSource
block|{
DECL|field|source
specifier|protected
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|field|m
specifier|protected
specifier|final
name|float
name|m
decl_stmt|;
DECL|field|a
specifier|protected
specifier|final
name|float
name|a
decl_stmt|;
DECL|field|b
specifier|protected
specifier|final
name|float
name|b
decl_stmt|;
comment|/**    *  f(source) = a/(m*float(source)+b)    */
DECL|method|ReciprocalFloatFunction
specifier|public
name|ReciprocalFloatFunction
parameter_list|(
name|ValueSource
name|source
parameter_list|,
name|float
name|m
parameter_list|,
name|float
name|a
parameter_list|,
name|float
name|b
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|m
operator|=
name|m
expr_stmt|;
name|this
operator|.
name|a
operator|=
name|a
expr_stmt|;
name|this
operator|.
name|b
operator|=
name|b
expr_stmt|;
block|}
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValues
name|vals
init|=
name|source
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|a
operator|/
operator|(
name|m
operator|*
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
operator|+
name|b
operator|)
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|a
argument_list|)
operator|+
literal|"/("
operator|+
name|m
operator|+
literal|"*float("
operator|+
name|vals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|')'
operator|+
literal|'+'
operator|+
name|b
operator|+
literal|')'
return|;
block|}
block|}
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
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|source
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|a
argument_list|)
operator|+
literal|"/("
operator|+
name|m
operator|+
literal|"*float("
operator|+
name|source
operator|.
name|description
argument_list|()
operator|+
literal|")"
operator|+
literal|"+"
operator|+
name|b
operator|+
literal|')'
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|a
argument_list|)
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|13
operator|)
operator||
operator|(
name|h
operator|>>>
literal|20
operator|)
expr_stmt|;
return|return
name|h
operator|+
operator|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|b
argument_list|)
operator|)
operator|+
name|source
operator|.
name|hashCode
argument_list|()
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
name|ReciprocalFloatFunction
operator|.
name|class
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ReciprocalFloatFunction
name|other
init|=
operator|(
name|ReciprocalFloatFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|m
operator|==
name|other
operator|.
name|m
operator|&&
name|this
operator|.
name|a
operator|==
name|other
operator|.
name|a
operator|&&
name|this
operator|.
name|b
operator|==
name|other
operator|.
name|b
operator|&&
name|this
operator|.
name|source
operator|.
name|equals
argument_list|(
name|other
operator|.
name|source
argument_list|)
return|;
block|}
block|}
end_class

end_unit


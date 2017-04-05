begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr.norm
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|norm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_comment
comment|/**  * A Normalizer to scale a feature value around an average-and-standard-deviation distribution.  *<p>  * Example configuration:<pre> "norm" : {     "class" : "org.apache.solr.ltr.norm.StandardNormalizer",     "params" : { "avg":"42", "std":"6" } }</pre>  *<p>  * Example normalizations:  *<ul>  *<li>39 will be normalized to -0.5  *<li>42 will be normalized to  0  *<li>45 will be normalized to +0.5  *</ul>  */
end_comment

begin_class
DECL|class|StandardNormalizer
specifier|public
class|class
name|StandardNormalizer
extends|extends
name|Normalizer
block|{
DECL|field|avg
specifier|private
name|float
name|avg
init|=
literal|0f
decl_stmt|;
DECL|field|std
specifier|private
name|float
name|std
init|=
literal|1f
decl_stmt|;
DECL|method|getAvg
specifier|public
name|float
name|getAvg
parameter_list|()
block|{
return|return
name|avg
return|;
block|}
DECL|method|setAvg
specifier|public
name|void
name|setAvg
parameter_list|(
name|float
name|avg
parameter_list|)
block|{
name|this
operator|.
name|avg
operator|=
name|avg
expr_stmt|;
block|}
DECL|method|getStd
specifier|public
name|float
name|getStd
parameter_list|()
block|{
return|return
name|std
return|;
block|}
DECL|method|setStd
specifier|public
name|void
name|setStd
parameter_list|(
name|float
name|std
parameter_list|)
block|{
name|this
operator|.
name|std
operator|=
name|std
expr_stmt|;
block|}
DECL|method|setAvg
specifier|public
name|void
name|setAvg
parameter_list|(
name|String
name|avg
parameter_list|)
block|{
name|this
operator|.
name|avg
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|avg
argument_list|)
expr_stmt|;
block|}
DECL|method|setStd
specifier|public
name|void
name|setStd
parameter_list|(
name|String
name|std
parameter_list|)
block|{
name|this
operator|.
name|std
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|std
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|float
name|normalize
parameter_list|(
name|float
name|value
parameter_list|)
block|{
return|return
operator|(
name|value
operator|-
name|avg
operator|)
operator|/
name|std
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|protected
name|void
name|validate
parameter_list|()
throws|throws
name|NormalizerException
block|{
if|if
condition|(
name|std
operator|<=
literal|0f
condition|)
block|{
throw|throw
operator|new
name|NormalizerException
argument_list|(
literal|"Standard Normalizer standard deviation must "
operator|+
literal|"be positive | avg = "
operator|+
name|avg
operator|+
literal|",std = "
operator|+
name|std
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|paramsToMap
specifier|public
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|paramsToMap
parameter_list|()
block|{
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
literal|2
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"avg"
argument_list|,
literal|'"'
operator|+
name|Float
operator|.
name|toString
argument_list|(
name|avg
argument_list|)
operator|+
literal|'"'
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"std"
argument_list|,
literal|'"'
operator|+
name|Float
operator|.
name|toString
argument_list|(
name|std
argument_list|)
operator|+
literal|'"'
argument_list|)
expr_stmt|;
return|return
name|params
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|64
argument_list|)
decl_stmt|;
comment|// default initialCapacity of 16 won't be enough
name|sb
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"avg="
argument_list|)
operator|.
name|append
argument_list|(
name|avg
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",std="
argument_list|)
operator|.
name|append
argument_list|(
name|avg
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit


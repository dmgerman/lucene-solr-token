begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.original.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|original
operator|.
name|config
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryParser
operator|.
name|original
operator|.
name|processors
operator|.
name|ParametricRangeQueryNodeProcessor
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
name|TermRangeQuery
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
name|AttributeImpl
import|;
end_import

begin_comment
comment|/**  * This attribute is used by {@link ParametricRangeQueryNodeProcessor} processor  * and must be defined in the {@link QueryConfigHandler}. This attribute tells  * the processor which {@link Collator} should be used for a  * {@link TermRangeQuery}<br/>  *   * @see org.apache.lucene.queryParser.original.config.RangeCollatorAttribute  */
end_comment

begin_class
DECL|class|RangeCollatorAttributeImpl
specifier|public
class|class
name|RangeCollatorAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|RangeCollatorAttribute
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6804360312723049526L
decl_stmt|;
DECL|field|rangeCollator
specifier|private
name|Collator
name|rangeCollator
decl_stmt|;
DECL|method|RangeCollatorAttributeImpl
specifier|public
name|RangeCollatorAttributeImpl
parameter_list|()
block|{
name|rangeCollator
operator|=
literal|null
expr_stmt|;
comment|// default value for 2.4
block|}
DECL|method|setDateResolution
specifier|public
name|void
name|setDateResolution
parameter_list|(
name|Collator
name|rangeCollator
parameter_list|)
block|{
name|this
operator|.
name|rangeCollator
operator|=
name|rangeCollator
expr_stmt|;
block|}
DECL|method|getRangeCollator
specifier|public
name|Collator
name|getRangeCollator
parameter_list|()
block|{
return|return
name|this
operator|.
name|rangeCollator
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|RangeCollatorAttributeImpl
condition|)
block|{
name|RangeCollatorAttributeImpl
name|rangeCollatorAttr
init|=
operator|(
name|RangeCollatorAttributeImpl
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|rangeCollatorAttr
operator|.
name|rangeCollator
operator|==
name|this
operator|.
name|rangeCollator
operator|||
name|rangeCollatorAttr
operator|.
name|rangeCollator
operator|.
name|equals
argument_list|(
name|this
operator|.
name|rangeCollator
argument_list|)
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
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|rangeCollator
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|this
operator|.
name|rangeCollator
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<rangeCollatorAttribute rangeCollator='"
operator|+
name|this
operator|.
name|rangeCollator
operator|+
literal|"'/>"
return|;
block|}
block|}
end_class

end_unit


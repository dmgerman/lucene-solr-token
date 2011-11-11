begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *<p>A MergeInfo provides information required for a MERGE context.  *  It is used as part of an {@link IOContext} in case of MERGE context.</p>  */
end_comment

begin_class
DECL|class|MergeInfo
specifier|public
class|class
name|MergeInfo
block|{
DECL|field|totalDocCount
specifier|public
specifier|final
name|int
name|totalDocCount
decl_stmt|;
DECL|field|estimatedMergeBytes
specifier|public
specifier|final
name|long
name|estimatedMergeBytes
decl_stmt|;
DECL|field|isExternal
specifier|public
specifier|final
name|boolean
name|isExternal
decl_stmt|;
DECL|field|mergeMaxNumSegments
specifier|public
specifier|final
name|int
name|mergeMaxNumSegments
decl_stmt|;
comment|/**    *<p>Creates a new {@link MergeInfo} instance from    * the values required for a MERGE {@link IOContext} context.    *     * These values are only estimates and are not the actual values.    *     */
DECL|method|MergeInfo
specifier|public
name|MergeInfo
parameter_list|(
name|int
name|totalDocCount
parameter_list|,
name|long
name|estimatedMergeBytes
parameter_list|,
name|boolean
name|isExternal
parameter_list|,
name|int
name|mergeMaxNumSegments
parameter_list|)
block|{
name|this
operator|.
name|totalDocCount
operator|=
name|totalDocCount
expr_stmt|;
name|this
operator|.
name|estimatedMergeBytes
operator|=
name|estimatedMergeBytes
expr_stmt|;
name|this
operator|.
name|isExternal
operator|=
name|isExternal
expr_stmt|;
name|this
operator|.
name|mergeMaxNumSegments
operator|=
name|mergeMaxNumSegments
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|estimatedMergeBytes
operator|^
operator|(
name|estimatedMergeBytes
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|isExternal
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|mergeMaxNumSegments
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|totalDocCount
expr_stmt|;
return|return
name|result
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MergeInfo
name|other
init|=
operator|(
name|MergeInfo
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|estimatedMergeBytes
operator|!=
name|other
operator|.
name|estimatedMergeBytes
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|isExternal
operator|!=
name|other
operator|.
name|isExternal
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|mergeMaxNumSegments
operator|!=
name|other
operator|.
name|mergeMaxNumSegments
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|totalDocCount
operator|!=
name|other
operator|.
name|totalDocCount
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
literal|"MergeInfo [totalDocCount="
operator|+
name|totalDocCount
operator|+
literal|", estimatedMergeBytes="
operator|+
name|estimatedMergeBytes
operator|+
literal|", isExternal="
operator|+
name|isExternal
operator|+
literal|", mergeMaxNumSegments="
operator|+
name|mergeMaxNumSegments
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit


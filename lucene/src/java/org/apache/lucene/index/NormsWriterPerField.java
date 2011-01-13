begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ArrayUtil
import|;
end_import

begin_comment
comment|/** Taps into DocInverter, as an InvertedDocEndConsumer,  *  which is called at the end of inverting each field.  We  *  just look at the length for the field (docState.length)  *  and record the norm. */
end_comment

begin_class
DECL|class|NormsWriterPerField
specifier|final
class|class
name|NormsWriterPerField
extends|extends
name|InvertedDocEndConsumerPerField
implements|implements
name|Comparable
argument_list|<
name|NormsWriterPerField
argument_list|>
block|{
DECL|field|perThread
specifier|final
name|NormsWriterPerThread
name|perThread
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
comment|// Holds all docID/norm pairs we've seen
DECL|field|docIDs
name|int
index|[]
name|docIDs
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
DECL|field|norms
name|byte
index|[]
name|norms
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
comment|// Shrink back if we are overallocated now:
name|docIDs
operator|=
name|ArrayUtil
operator|.
name|shrink
argument_list|(
name|docIDs
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|norms
operator|=
name|ArrayUtil
operator|.
name|shrink
argument_list|(
name|norms
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|NormsWriterPerField
specifier|public
name|NormsWriterPerField
parameter_list|(
specifier|final
name|DocInverterPerField
name|docInverterPerField
parameter_list|,
specifier|final
name|NormsWriterPerThread
name|perThread
parameter_list|,
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|perThread
operator|=
name|perThread
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|docState
operator|=
name|perThread
operator|.
name|docState
expr_stmt|;
name|fieldState
operator|=
name|docInverterPerField
operator|.
name|fieldState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
name|upto
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|NormsWriterPerField
name|other
parameter_list|)
block|{
return|return
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|isIndexed
operator|&&
operator|!
name|fieldInfo
operator|.
name|omitNorms
condition|)
block|{
if|if
condition|(
name|docIDs
operator|.
name|length
operator|<=
name|upto
condition|)
block|{
assert|assert
name|docIDs
operator|.
name|length
operator|==
name|upto
assert|;
name|docIDs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docIDs
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|norms
operator|.
name|length
operator|<=
name|upto
condition|)
block|{
assert|assert
name|norms
operator|.
name|length
operator|==
name|upto
assert|;
name|norms
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|norms
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
specifier|final
name|float
name|norm
init|=
name|docState
operator|.
name|similarity
operator|.
name|computeNorm
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|fieldState
argument_list|)
decl_stmt|;
name|norms
index|[
name|upto
index|]
operator|=
name|docState
operator|.
name|similarity
operator|.
name|encodeNormValue
argument_list|(
name|norm
argument_list|)
expr_stmt|;
name|docIDs
index|[
name|upto
index|]
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


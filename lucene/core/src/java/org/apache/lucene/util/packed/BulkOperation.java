begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// This file has been automatically generated, DO NOT EDIT
end_comment

begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Efficient sequential read/write of packed integers.  */
end_comment

begin_class
DECL|class|BulkOperation
specifier|abstract
class|class
name|BulkOperation
implements|implements
name|PackedInts
operator|.
name|Decoder
implements|,
name|PackedInts
operator|.
name|Encoder
block|{
DECL|field|packed1
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked1
name|packed1
init|=
operator|new
name|BulkOperationPacked1
argument_list|()
decl_stmt|;
DECL|field|packed2
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked2
name|packed2
init|=
operator|new
name|BulkOperationPacked2
argument_list|()
decl_stmt|;
DECL|field|packed3
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked3
name|packed3
init|=
operator|new
name|BulkOperationPacked3
argument_list|()
decl_stmt|;
DECL|field|packed4
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked4
name|packed4
init|=
operator|new
name|BulkOperationPacked4
argument_list|()
decl_stmt|;
DECL|field|packed5
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked5
name|packed5
init|=
operator|new
name|BulkOperationPacked5
argument_list|()
decl_stmt|;
DECL|field|packed6
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked6
name|packed6
init|=
operator|new
name|BulkOperationPacked6
argument_list|()
decl_stmt|;
DECL|field|packed7
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked7
name|packed7
init|=
operator|new
name|BulkOperationPacked7
argument_list|()
decl_stmt|;
DECL|field|packed8
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked8
name|packed8
init|=
operator|new
name|BulkOperationPacked8
argument_list|()
decl_stmt|;
DECL|field|packed9
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked9
name|packed9
init|=
operator|new
name|BulkOperationPacked9
argument_list|()
decl_stmt|;
DECL|field|packed10
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked10
name|packed10
init|=
operator|new
name|BulkOperationPacked10
argument_list|()
decl_stmt|;
DECL|field|packed11
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked11
name|packed11
init|=
operator|new
name|BulkOperationPacked11
argument_list|()
decl_stmt|;
DECL|field|packed12
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked12
name|packed12
init|=
operator|new
name|BulkOperationPacked12
argument_list|()
decl_stmt|;
DECL|field|packed13
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked13
name|packed13
init|=
operator|new
name|BulkOperationPacked13
argument_list|()
decl_stmt|;
DECL|field|packed14
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked14
name|packed14
init|=
operator|new
name|BulkOperationPacked14
argument_list|()
decl_stmt|;
DECL|field|packed15
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked15
name|packed15
init|=
operator|new
name|BulkOperationPacked15
argument_list|()
decl_stmt|;
DECL|field|packed16
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked16
name|packed16
init|=
operator|new
name|BulkOperationPacked16
argument_list|()
decl_stmt|;
DECL|field|packed17
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked17
name|packed17
init|=
operator|new
name|BulkOperationPacked17
argument_list|()
decl_stmt|;
DECL|field|packed18
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked18
name|packed18
init|=
operator|new
name|BulkOperationPacked18
argument_list|()
decl_stmt|;
DECL|field|packed19
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked19
name|packed19
init|=
operator|new
name|BulkOperationPacked19
argument_list|()
decl_stmt|;
DECL|field|packed20
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked20
name|packed20
init|=
operator|new
name|BulkOperationPacked20
argument_list|()
decl_stmt|;
DECL|field|packed21
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked21
name|packed21
init|=
operator|new
name|BulkOperationPacked21
argument_list|()
decl_stmt|;
DECL|field|packed22
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked22
name|packed22
init|=
operator|new
name|BulkOperationPacked22
argument_list|()
decl_stmt|;
DECL|field|packed23
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked23
name|packed23
init|=
operator|new
name|BulkOperationPacked23
argument_list|()
decl_stmt|;
DECL|field|packed24
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked24
name|packed24
init|=
operator|new
name|BulkOperationPacked24
argument_list|()
decl_stmt|;
DECL|field|packed25
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked25
name|packed25
init|=
operator|new
name|BulkOperationPacked25
argument_list|()
decl_stmt|;
DECL|field|packed26
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked26
name|packed26
init|=
operator|new
name|BulkOperationPacked26
argument_list|()
decl_stmt|;
DECL|field|packed27
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked27
name|packed27
init|=
operator|new
name|BulkOperationPacked27
argument_list|()
decl_stmt|;
DECL|field|packed28
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked28
name|packed28
init|=
operator|new
name|BulkOperationPacked28
argument_list|()
decl_stmt|;
DECL|field|packed29
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked29
name|packed29
init|=
operator|new
name|BulkOperationPacked29
argument_list|()
decl_stmt|;
DECL|field|packed30
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked30
name|packed30
init|=
operator|new
name|BulkOperationPacked30
argument_list|()
decl_stmt|;
DECL|field|packed31
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked31
name|packed31
init|=
operator|new
name|BulkOperationPacked31
argument_list|()
decl_stmt|;
DECL|field|packed32
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked32
name|packed32
init|=
operator|new
name|BulkOperationPacked32
argument_list|()
decl_stmt|;
DECL|field|packed33
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked33
name|packed33
init|=
operator|new
name|BulkOperationPacked33
argument_list|()
decl_stmt|;
DECL|field|packed34
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked34
name|packed34
init|=
operator|new
name|BulkOperationPacked34
argument_list|()
decl_stmt|;
DECL|field|packed35
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked35
name|packed35
init|=
operator|new
name|BulkOperationPacked35
argument_list|()
decl_stmt|;
DECL|field|packed36
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked36
name|packed36
init|=
operator|new
name|BulkOperationPacked36
argument_list|()
decl_stmt|;
DECL|field|packed37
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked37
name|packed37
init|=
operator|new
name|BulkOperationPacked37
argument_list|()
decl_stmt|;
DECL|field|packed38
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked38
name|packed38
init|=
operator|new
name|BulkOperationPacked38
argument_list|()
decl_stmt|;
DECL|field|packed39
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked39
name|packed39
init|=
operator|new
name|BulkOperationPacked39
argument_list|()
decl_stmt|;
DECL|field|packed40
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked40
name|packed40
init|=
operator|new
name|BulkOperationPacked40
argument_list|()
decl_stmt|;
DECL|field|packed41
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked41
name|packed41
init|=
operator|new
name|BulkOperationPacked41
argument_list|()
decl_stmt|;
DECL|field|packed42
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked42
name|packed42
init|=
operator|new
name|BulkOperationPacked42
argument_list|()
decl_stmt|;
DECL|field|packed43
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked43
name|packed43
init|=
operator|new
name|BulkOperationPacked43
argument_list|()
decl_stmt|;
DECL|field|packed44
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked44
name|packed44
init|=
operator|new
name|BulkOperationPacked44
argument_list|()
decl_stmt|;
DECL|field|packed45
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked45
name|packed45
init|=
operator|new
name|BulkOperationPacked45
argument_list|()
decl_stmt|;
DECL|field|packed46
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked46
name|packed46
init|=
operator|new
name|BulkOperationPacked46
argument_list|()
decl_stmt|;
DECL|field|packed47
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked47
name|packed47
init|=
operator|new
name|BulkOperationPacked47
argument_list|()
decl_stmt|;
DECL|field|packed48
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked48
name|packed48
init|=
operator|new
name|BulkOperationPacked48
argument_list|()
decl_stmt|;
DECL|field|packed49
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked49
name|packed49
init|=
operator|new
name|BulkOperationPacked49
argument_list|()
decl_stmt|;
DECL|field|packed50
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked50
name|packed50
init|=
operator|new
name|BulkOperationPacked50
argument_list|()
decl_stmt|;
DECL|field|packed51
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked51
name|packed51
init|=
operator|new
name|BulkOperationPacked51
argument_list|()
decl_stmt|;
DECL|field|packed52
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked52
name|packed52
init|=
operator|new
name|BulkOperationPacked52
argument_list|()
decl_stmt|;
DECL|field|packed53
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked53
name|packed53
init|=
operator|new
name|BulkOperationPacked53
argument_list|()
decl_stmt|;
DECL|field|packed54
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked54
name|packed54
init|=
operator|new
name|BulkOperationPacked54
argument_list|()
decl_stmt|;
DECL|field|packed55
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked55
name|packed55
init|=
operator|new
name|BulkOperationPacked55
argument_list|()
decl_stmt|;
DECL|field|packed56
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked56
name|packed56
init|=
operator|new
name|BulkOperationPacked56
argument_list|()
decl_stmt|;
DECL|field|packed57
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked57
name|packed57
init|=
operator|new
name|BulkOperationPacked57
argument_list|()
decl_stmt|;
DECL|field|packed58
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked58
name|packed58
init|=
operator|new
name|BulkOperationPacked58
argument_list|()
decl_stmt|;
DECL|field|packed59
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked59
name|packed59
init|=
operator|new
name|BulkOperationPacked59
argument_list|()
decl_stmt|;
DECL|field|packed60
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked60
name|packed60
init|=
operator|new
name|BulkOperationPacked60
argument_list|()
decl_stmt|;
DECL|field|packed61
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked61
name|packed61
init|=
operator|new
name|BulkOperationPacked61
argument_list|()
decl_stmt|;
DECL|field|packed62
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked62
name|packed62
init|=
operator|new
name|BulkOperationPacked62
argument_list|()
decl_stmt|;
DECL|field|packed63
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked63
name|packed63
init|=
operator|new
name|BulkOperationPacked63
argument_list|()
decl_stmt|;
DECL|field|packed64
specifier|private
specifier|static
specifier|final
name|BulkOperationPacked64
name|packed64
init|=
operator|new
name|BulkOperationPacked64
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock1
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock1
name|packedSingleBlock1
init|=
operator|new
name|BulkOperationPackedSingleBlock1
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock2
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock2
name|packedSingleBlock2
init|=
operator|new
name|BulkOperationPackedSingleBlock2
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock3
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock3
name|packedSingleBlock3
init|=
operator|new
name|BulkOperationPackedSingleBlock3
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock4
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock4
name|packedSingleBlock4
init|=
operator|new
name|BulkOperationPackedSingleBlock4
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock5
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock5
name|packedSingleBlock5
init|=
operator|new
name|BulkOperationPackedSingleBlock5
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock6
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock6
name|packedSingleBlock6
init|=
operator|new
name|BulkOperationPackedSingleBlock6
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock7
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock7
name|packedSingleBlock7
init|=
operator|new
name|BulkOperationPackedSingleBlock7
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock8
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock8
name|packedSingleBlock8
init|=
operator|new
name|BulkOperationPackedSingleBlock8
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock9
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock9
name|packedSingleBlock9
init|=
operator|new
name|BulkOperationPackedSingleBlock9
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock10
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock10
name|packedSingleBlock10
init|=
operator|new
name|BulkOperationPackedSingleBlock10
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock12
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock12
name|packedSingleBlock12
init|=
operator|new
name|BulkOperationPackedSingleBlock12
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock16
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock16
name|packedSingleBlock16
init|=
operator|new
name|BulkOperationPackedSingleBlock16
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock21
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock21
name|packedSingleBlock21
init|=
operator|new
name|BulkOperationPackedSingleBlock21
argument_list|()
decl_stmt|;
DECL|field|packedSingleBlock32
specifier|private
specifier|static
specifier|final
name|BulkOperationPackedSingleBlock32
name|packedSingleBlock32
init|=
operator|new
name|BulkOperationPackedSingleBlock32
argument_list|()
decl_stmt|;
DECL|method|of
specifier|public
specifier|static
name|BulkOperation
name|of
parameter_list|(
name|PackedInts
operator|.
name|Format
name|format
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|PACKED
case|:
switch|switch
condition|(
name|bitsPerValue
condition|)
block|{
case|case
literal|1
case|:
return|return
name|packed1
return|;
case|case
literal|2
case|:
return|return
name|packed2
return|;
case|case
literal|3
case|:
return|return
name|packed3
return|;
case|case
literal|4
case|:
return|return
name|packed4
return|;
case|case
literal|5
case|:
return|return
name|packed5
return|;
case|case
literal|6
case|:
return|return
name|packed6
return|;
case|case
literal|7
case|:
return|return
name|packed7
return|;
case|case
literal|8
case|:
return|return
name|packed8
return|;
case|case
literal|9
case|:
return|return
name|packed9
return|;
case|case
literal|10
case|:
return|return
name|packed10
return|;
case|case
literal|11
case|:
return|return
name|packed11
return|;
case|case
literal|12
case|:
return|return
name|packed12
return|;
case|case
literal|13
case|:
return|return
name|packed13
return|;
case|case
literal|14
case|:
return|return
name|packed14
return|;
case|case
literal|15
case|:
return|return
name|packed15
return|;
case|case
literal|16
case|:
return|return
name|packed16
return|;
case|case
literal|17
case|:
return|return
name|packed17
return|;
case|case
literal|18
case|:
return|return
name|packed18
return|;
case|case
literal|19
case|:
return|return
name|packed19
return|;
case|case
literal|20
case|:
return|return
name|packed20
return|;
case|case
literal|21
case|:
return|return
name|packed21
return|;
case|case
literal|22
case|:
return|return
name|packed22
return|;
case|case
literal|23
case|:
return|return
name|packed23
return|;
case|case
literal|24
case|:
return|return
name|packed24
return|;
case|case
literal|25
case|:
return|return
name|packed25
return|;
case|case
literal|26
case|:
return|return
name|packed26
return|;
case|case
literal|27
case|:
return|return
name|packed27
return|;
case|case
literal|28
case|:
return|return
name|packed28
return|;
case|case
literal|29
case|:
return|return
name|packed29
return|;
case|case
literal|30
case|:
return|return
name|packed30
return|;
case|case
literal|31
case|:
return|return
name|packed31
return|;
case|case
literal|32
case|:
return|return
name|packed32
return|;
case|case
literal|33
case|:
return|return
name|packed33
return|;
case|case
literal|34
case|:
return|return
name|packed34
return|;
case|case
literal|35
case|:
return|return
name|packed35
return|;
case|case
literal|36
case|:
return|return
name|packed36
return|;
case|case
literal|37
case|:
return|return
name|packed37
return|;
case|case
literal|38
case|:
return|return
name|packed38
return|;
case|case
literal|39
case|:
return|return
name|packed39
return|;
case|case
literal|40
case|:
return|return
name|packed40
return|;
case|case
literal|41
case|:
return|return
name|packed41
return|;
case|case
literal|42
case|:
return|return
name|packed42
return|;
case|case
literal|43
case|:
return|return
name|packed43
return|;
case|case
literal|44
case|:
return|return
name|packed44
return|;
case|case
literal|45
case|:
return|return
name|packed45
return|;
case|case
literal|46
case|:
return|return
name|packed46
return|;
case|case
literal|47
case|:
return|return
name|packed47
return|;
case|case
literal|48
case|:
return|return
name|packed48
return|;
case|case
literal|49
case|:
return|return
name|packed49
return|;
case|case
literal|50
case|:
return|return
name|packed50
return|;
case|case
literal|51
case|:
return|return
name|packed51
return|;
case|case
literal|52
case|:
return|return
name|packed52
return|;
case|case
literal|53
case|:
return|return
name|packed53
return|;
case|case
literal|54
case|:
return|return
name|packed54
return|;
case|case
literal|55
case|:
return|return
name|packed55
return|;
case|case
literal|56
case|:
return|return
name|packed56
return|;
case|case
literal|57
case|:
return|return
name|packed57
return|;
case|case
literal|58
case|:
return|return
name|packed58
return|;
case|case
literal|59
case|:
return|return
name|packed59
return|;
case|case
literal|60
case|:
return|return
name|packed60
return|;
case|case
literal|61
case|:
return|return
name|packed61
return|;
case|case
literal|62
case|:
return|return
name|packed62
return|;
case|case
literal|63
case|:
return|return
name|packed63
return|;
case|case
literal|64
case|:
return|return
name|packed64
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
case|case
name|PACKED_SINGLE_BLOCK
case|:
switch|switch
condition|(
name|bitsPerValue
condition|)
block|{
case|case
literal|1
case|:
return|return
name|packedSingleBlock1
return|;
case|case
literal|2
case|:
return|return
name|packedSingleBlock2
return|;
case|case
literal|3
case|:
return|return
name|packedSingleBlock3
return|;
case|case
literal|4
case|:
return|return
name|packedSingleBlock4
return|;
case|case
literal|5
case|:
return|return
name|packedSingleBlock5
return|;
case|case
literal|6
case|:
return|return
name|packedSingleBlock6
return|;
case|case
literal|7
case|:
return|return
name|packedSingleBlock7
return|;
case|case
literal|8
case|:
return|return
name|packedSingleBlock8
return|;
case|case
literal|9
case|:
return|return
name|packedSingleBlock9
return|;
case|case
literal|10
case|:
return|return
name|packedSingleBlock10
return|;
case|case
literal|12
case|:
return|return
name|packedSingleBlock12
return|;
case|case
literal|16
case|:
return|return
name|packedSingleBlock16
return|;
case|case
literal|21
case|:
return|return
name|packedSingleBlock21
return|;
case|case
literal|32
case|:
return|return
name|packedSingleBlock32
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
DECL|method|toLongArray
specifier|private
specifier|static
name|long
index|[]
name|toLongArray
parameter_list|(
name|int
index|[]
name|ints
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|long
index|[]
name|arr
init|=
operator|new
name|long
index|[
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
name|ints
index|[
name|offset
operator|+
name|i
index|]
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|encode
argument_list|(
name|toLongArray
argument_list|(
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|iterations
operator|*
name|valueCount
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|longBLocks
init|=
operator|new
name|long
index|[
name|blockCount
argument_list|()
operator|*
name|iterations
index|]
decl_stmt|;
name|encode
argument_list|(
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|longBLocks
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
argument_list|)
operator|.
name|asLongBuffer
argument_list|()
operator|.
name|put
argument_list|(
name|longBLocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|longBLocks
init|=
operator|new
name|long
index|[
name|blockCount
argument_list|()
operator|*
name|iterations
index|]
decl_stmt|;
name|encode
argument_list|(
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|longBLocks
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
argument_list|)
operator|.
name|asLongBuffer
argument_list|()
operator|.
name|put
argument_list|(
name|longBLocks
argument_list|)
expr_stmt|;
block|}
comment|/**    * For every number of bits per value, there is a minimum number of    * blocks (b) / values (v) you need to write in order to reach the next block    * boundary:    *  - 16 bits per value -> b=1, v=4    *  - 24 bits per value -> b=3, v=8    *  - 50 bits per value -> b=25, v=32    *  - 63 bits per value -> b=63, v=64    *  - ...    *    * A bulk read consists in copying<code>iterations*v</code> values that are    * contained in<code>iterations*b</code> blocks into a<code>long[]</code>    * (higher values of<code>iterations</code> are likely to yield a better    * throughput) => this requires n * (b + v) longs in memory.    *    * This method computes<code>iterations</code> as    *<code>ramBudget / (8 * (b + v))</code> (since a long is 8 bytes).    */
DECL|method|computeIterations
specifier|public
specifier|final
name|int
name|computeIterations
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|ramBudget
parameter_list|)
block|{
specifier|final
name|int
name|iterations
init|=
operator|(
name|ramBudget
operator|>>>
literal|3
operator|)
operator|/
operator|(
name|blockCount
argument_list|()
operator|+
name|valueCount
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|iterations
operator|==
literal|0
condition|)
block|{
comment|// at least 1
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|iterations
operator|-
literal|1
operator|)
operator|*
name|blockCount
argument_list|()
operator|>=
name|valueCount
condition|)
block|{
comment|// don't allocate for more than the size of the reader
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|valueCount
operator|/
name|valueCount
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|iterations
return|;
block|}
block|}
block|}
end_class

end_unit


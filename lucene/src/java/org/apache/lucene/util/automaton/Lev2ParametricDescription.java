begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|// The following code was generated with the moman/finenight pkg
end_comment

begin_comment
comment|// This package is available under the MIT License, see NOTICE.txt
end_comment

begin_comment
comment|// for more details.
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
name|automaton
operator|.
name|LevenshteinAutomata
operator|.
name|ParametricDescription
import|;
end_import

begin_comment
comment|/** Parametric description for generating a Levenshtein automaton of degree 2 */
end_comment

begin_class
DECL|class|Lev2ParametricDescription
class|class
name|Lev2ParametricDescription
extends|extends
name|ParametricDescription
block|{
annotation|@
name|Override
DECL|method|transition
name|int
name|transition
parameter_list|(
name|int
name|absState
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|vector
parameter_list|)
block|{
comment|// null absState should never be passed in
assert|assert
name|absState
operator|!=
operator|-
literal|1
assert|;
comment|// decode absState -> state, offset
name|int
name|state
init|=
name|absState
operator|/
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|offset
init|=
name|absState
operator|%
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
assert|assert
name|offset
operator|>=
literal|0
assert|;
if|if
condition|(
name|position
operator|==
name|w
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|3
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|3
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs0
argument_list|,
name|loc
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates0
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|5
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|5
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs1
argument_list|,
name|loc
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates1
argument_list|,
name|loc
argument_list|,
literal|3
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|2
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|11
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|11
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs2
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates2
argument_list|,
name|loc
argument_list|,
literal|4
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|3
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|21
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|21
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs3
argument_list|,
name|loc
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates3
argument_list|,
name|loc
argument_list|,
literal|5
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|w
operator|-
literal|4
condition|)
block|{
if|if
condition|(
name|state
operator|<
literal|30
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|30
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs4
argument_list|,
name|loc
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates4
argument_list|,
name|loc
argument_list|,
literal|5
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|state
operator|<
literal|30
condition|)
block|{
specifier|final
name|int
name|loc
init|=
name|vector
operator|*
literal|30
operator|+
name|state
decl_stmt|;
name|offset
operator|+=
name|unpack
argument_list|(
name|offsetIncrs5
argument_list|,
name|loc
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|state
operator|=
name|unpack
argument_list|(
name|toStates5
argument_list|,
name|loc
argument_list|,
literal|5
argument_list|)
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|state
operator|==
operator|-
literal|1
condition|)
block|{
comment|// null state
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
comment|// translate back to abs
return|return
name|state
operator|*
operator|(
name|w
operator|+
literal|1
operator|)
operator|+
name|offset
return|;
block|}
block|}
comment|// 1 vectors; 3 states per vector; array length = 3
DECL|field|toStates0
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates0
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x23L
block|}
decl_stmt|;
DECL|field|offsetIncrs0
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs0
init|=
operator|new
name|long
index|[]
comment|/*1 bits per value */
block|{
literal|0x0L
block|}
decl_stmt|;
comment|// 2 vectors; 5 states per vector; array length = 10
DECL|field|toStates1
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates1
init|=
operator|new
name|long
index|[]
comment|/*3 bits per value */
block|{
literal|0x1a68c105L
block|}
decl_stmt|;
DECL|field|offsetIncrs1
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs1
init|=
operator|new
name|long
index|[]
comment|/*1 bits per value */
block|{
literal|0x3e0L
block|}
decl_stmt|;
comment|// 4 vectors; 11 states per vector; array length = 44
DECL|field|toStates2
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates2
init|=
operator|new
name|long
index|[]
comment|/*4 bits per value */
block|{
literal|0x6280b80804280405L
block|,
literal|0x2323432321608282L
block|,
literal|0x523434543213L
block|}
decl_stmt|;
DECL|field|offsetIncrs2
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs2
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x5555502220000800L
block|,
literal|0x555555L
block|}
decl_stmt|;
comment|// 8 vectors; 21 states per vector; array length = 168
DECL|field|toStates3
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates3
init|=
operator|new
name|long
index|[]
comment|/*5 bits per value */
block|{
literal|0x40300c0108801005L
block|,
literal|0x80202a8208801000L
block|,
literal|0x4021006280a0288dL
block|,
literal|0x30482184802d8414L
block|,
literal|0x5990240880010460L
block|,
literal|0x191a28118330900L
block|,
literal|0x310c413204c1104L
block|,
literal|0x8625084811c4710dL
block|,
literal|0xa92a398e2188231aL
block|,
literal|0x104e351c4a508ca4L
block|,
literal|0x21208511c8341483L
block|,
literal|0xe6290620946a1910L
block|,
literal|0xd47221423216a4a0L
block|,
literal|0x28L
block|}
decl_stmt|;
DECL|field|offsetIncrs3
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs3
init|=
operator|new
name|long
index|[]
comment|/*2 bits per value */
block|{
literal|0x33300030c2000800L
block|,
literal|0x32828088800c3cfL
block|,
literal|0x5555550cace32320L
block|,
literal|0x5555555555555555L
block|,
literal|0x5555555555555555L
block|,
literal|0x5555L
block|}
decl_stmt|;
comment|// 16 vectors; 30 states per vector; array length = 480
DECL|field|toStates4
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates4
init|=
operator|new
name|long
index|[]
comment|/*5 bits per value */
block|{
literal|0x80300c0108801005L
block|,
literal|0x88210802000L
block|,
literal|0x44200401400000L
block|,
literal|0x7ae3b88621185c07L
block|,
literal|0x101500042100404L
block|,
literal|0x20803140501446cL
block|,
literal|0x40100420006c2122L
block|,
literal|0x490140511b004054L
block|,
literal|0x8401f2e3c086411L
block|,
literal|0x120861200b100822L
block|,
literal|0x641102400081180cL
block|,
literal|0x4802c40100001088L
block|,
literal|0x8c21195607048418L
block|,
literal|0x1421014245bc3f2L
block|,
literal|0x23450230661200b1L
block|,
literal|0x2108664118240803L
block|,
literal|0x8c1984802c802004L
block|,
literal|0xbc3e28c41150d140L
block|,
literal|0xc4120102209421dL
block|,
literal|0x7884c11c4710d031L
block|,
literal|0x210842109031bc62L
block|,
literal|0xd21484360c431044L
block|,
literal|0x9c265293a3a6e741L
block|,
literal|0x1cc710c41109ce70L
block|,
literal|0x1bce27a846525495L
block|,
literal|0x3105425094a108c7L
block|,
literal|0x6f735e95254731c4L
block|,
literal|0x9ee7a9c234a9393aL
block|,
literal|0x144720d0520c4150L
block|,
literal|0x211051bc646084c2L
block|,
literal|0x3614831048220842L
block|,
literal|0x93a460e742351488L
block|,
literal|0xc4120a2e70a24656L
block|,
literal|0x284642d4941cc520L
block|,
literal|0x4094a210c51bce46L
block|,
literal|0xb525073148310502L
block|,
literal|0x24356939460f7358L
block|,
literal|0x4098e7aaL
block|}
decl_stmt|;
DECL|field|offsetIncrs4
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs4
init|=
operator|new
name|long
index|[]
comment|/*3 bits per value */
block|{
literal|0xc0602000010000L
block|,
literal|0xa000040000000001L
block|,
literal|0x248204041248L
block|,
literal|0xb0180c06c3618618L
block|,
literal|0x238d861860001861L
block|,
literal|0x41040061c6e06041L
block|,
literal|0x4004900c2402400L
block|,
literal|0x409489001041001L
block|,
literal|0x4184184004148124L
block|,
literal|0x1041b4980c24c3L
block|,
literal|0xd26040938d061061L
block|,
literal|0x2492492492494146L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x24924924L
block|}
decl_stmt|;
comment|// 32 vectors; 30 states per vector; array length = 960
DECL|field|toStates5
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|toStates5
init|=
operator|new
name|long
index|[]
comment|/*5 bits per value */
block|{
literal|0x80300c0108801005L
block|,
literal|0x88210802000L
block|,
literal|0x42200401400000L
block|,
literal|0xa088201000300c03L
block|,
literal|0x100510842108428L
block|,
literal|0x2188461701c01108L
block|,
literal|0x108401011eb8eeL
block|,
literal|0x85c0700442004014L
block|,
literal|0x88267ae3b886211L
block|,
literal|0x1446c01015108842L
block|,
literal|0xc212202080314050L
block|,
literal|0x405440100420006L
block|,
literal|0x10201c50140511b0L
block|,
literal|0x942528423b08888L
block|,
literal|0x240501446c010155L
block|,
literal|0x21007cb8f0219045L
block|,
literal|0x511b004054402088L
block|,
literal|0x2e3c086411490140L
block|,
literal|0x200b50904428823fL
block|,
literal|0x400081180c120861L
block|,
literal|0x100001088641102L
block|,
literal|0x46030482184802c4L
block|,
literal|0x9ce8990840980030L
block|,
literal|0x21061200b709c210L
block|,
literal|0xf0fca308465581c1L
block|,
literal|0x802c405084050916L
block|,
literal|0xc211956070484184L
block|,
literal|0x9e4209ee65bc3f28L
block|,
literal|0x3450230661200b70L
block|,
literal|0x1086641182408032L
block|,
literal|0xc1984802c8020042L
block|,
literal|0x86098201c8d1408L
block|,
literal|0xb88a22529ce399L
block|,
literal|0x1045434502306612L
block|,
literal|0x4088250876f0f8a3L
block|,
literal|0xd1408c1984802c80L
block|,
literal|0xee3dbc3e28c41150L
block|,
literal|0xd0310c4188984429L
block|,
literal|0xbc627884c11c4710L
block|,
literal|0x1044210842109031L
block|,
literal|0x21704711c4340c43L
block|,
literal|0xbdef7bdf0c7a18b4L
block|,
literal|0x85210d8310c41ef7L
block|,
literal|0x994a4e8e9b9d074L
block|,
literal|0x60c4310442739c27L
block|,
literal|0x3a3a6e741d214843L
block|,
literal|0x41ef77bdf77de529L
block|,
literal|0x8465254951cc710cL
block|,
literal|0x94a108c71bce27aL
block|,
literal|0x5254731c43105425L
block|,
literal|0xdb1c7a38b4a15949L
block|,
literal|0xc710c41cf73dce7bL
block|,
literal|0xe4e9bdcd7a54951cL
block|,
literal|0x5427b9ea708d2a4L
block|,
literal|0x735e95254731c431L
block|,
literal|0xbd677db4a9393a6fL
block|,
literal|0x4720d0520c41cf75L
block|,
literal|0x1051bc646084c214L
block|,
literal|0x1483104822084221L
block|,
literal|0x193821708511c834L
block|,
literal|0x1bf6fdef6f7f147aL
block|,
literal|0xd08d45220d8520c4L
block|,
literal|0x9c289195a4e91839L
block|,
literal|0x488361483104828bL
block|,
literal|0xe5693a460e742351L
block|,
literal|0x520c41bf71bdf717L
block|,
literal|0xe46284642d4941ccL
block|,
literal|0x5024094a210c51bcL
block|,
literal|0x590b525073148310L
block|,
literal|0xce6f7b147a3938a1L
block|,
literal|0x941cc520c41f77ddL
block|,
literal|0xd5a4e5183dcd62d4L
block|,
literal|0x48310502639ea890L
block|,
literal|0x460f7358b5250731L
block|,
literal|0xf779bd6717b56939L
block|}
decl_stmt|;
DECL|field|offsetIncrs5
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|offsetIncrs5
init|=
operator|new
name|long
index|[]
comment|/*3 bits per value */
block|{
literal|0xc0602000010000L
block|,
literal|0x8000040000000001L
block|,
literal|0xb6db6d4030180L
block|,
literal|0x810104922800010L
block|,
literal|0x248a000040000092L
block|,
literal|0x618000b649654041L
block|,
literal|0x861b0180c06c3618L
block|,
literal|0x301b0d861860001L
block|,
literal|0x61861800075d6ed6L
block|,
literal|0x1871b8181048e3L
block|,
literal|0xe56041238d861860L
block|,
literal|0x40240041040075c6L
block|,
literal|0x4100104004900c2L
block|,
literal|0x55b5240309009001L
block|,
literal|0x1025224004104005L
block|,
literal|0x10410010520490L
block|,
literal|0x55495240409489L
block|,
literal|0x4980c24c34184184L
block|,
literal|0x30d061061001041bL
block|,
literal|0x184005556d260309L
block|,
literal|0x51b4981024e34184L
block|,
literal|0x40938d0610610010L
block|,
literal|0x492492495546d260L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|,
literal|0x9249249249249249L
block|,
literal|0x4924924924924924L
block|,
literal|0x2492492492492492L
block|}
decl_stmt|;
comment|// state map
comment|//   0 -> [(0, 0)]
comment|//   1 -> [(0, 2)]
comment|//   2 -> [(0, 1)]
comment|//   3 -> [(0, 2), (1, 2)]
comment|//   4 -> [(0, 1), (1, 1)]
comment|//   5 -> [(0, 2), (2, 1)]
comment|//   6 -> [(0, 1), (2, 2)]
comment|//   7 -> [(0, 2), (1, 2), (2, 2)]
comment|//   8 -> [(0, 1), (2, 1)]
comment|//   9 -> [(0, 2), (2, 2)]
comment|//   10 -> [(0, 1), (1, 1), (2, 1)]
comment|//   11 -> [(0, 2), (1, 2), (2, 2), (3, 2)]
comment|//   12 -> [(0, 2), (2, 1), (3, 1)]
comment|//   13 -> [(0, 2), (3, 2)]
comment|//   14 -> [(0, 2), (2, 2), (3, 2)]
comment|//   15 -> [(0, 2), (1, 2), (3, 1)]
comment|//   16 -> [(0, 2), (1, 2), (3, 2)]
comment|//   17 -> [(0, 1), (2, 2), (3, 2)]
comment|//   18 -> [(0, 2), (3, 1)]
comment|//   19 -> [(0, 1), (3, 2)]
comment|//   20 -> [(0, 1), (1, 1), (3, 2)]
comment|//   21 -> [(0, 2), (2, 1), (4, 2)]
comment|//   22 -> [(0, 2), (1, 2), (4, 2)]
comment|//   23 -> [(0, 2), (1, 2), (3, 2), (4, 2)]
comment|//   24 -> [(0, 2), (2, 2), (4, 2)]
comment|//   25 -> [(0, 2), (2, 2), (3, 2), (4, 2)]
comment|//   26 -> [(0, 2), (3, 2), (4, 2)]
comment|//   27 -> [(0, 2), (1, 2), (2, 2), (3, 2), (4, 2)]
comment|//   28 -> [(0, 2), (4, 2)]
comment|//   29 -> [(0, 2), (1, 2), (2, 2), (4, 2)]
DECL|method|Lev2ParametricDescription
specifier|public
name|Lev2ParametricDescription
parameter_list|(
name|int
name|w
parameter_list|)
block|{
name|super
argument_list|(
name|w
argument_list|,
literal|2
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
operator|-
literal|1
block|,
literal|0
block|,
literal|0
block|,
operator|-
literal|1
block|,
literal|0
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


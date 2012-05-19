begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Set
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
name|codecs
operator|.
name|LiveDocsFormat
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
name|IndexFileNames
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
name|SegmentInfo
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
name|store
operator|.
name|DataOutput
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|store
operator|.
name|IOContext
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
name|Bits
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
name|CodecUtil
import|;
end_import

begin_comment
comment|// javadocs
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
name|MutableBits
import|;
end_import

begin_comment
comment|/**  * Lucene 4.0 Live Documents Format.  *<p>  *<p>The .del file is optional, and only exists when a segment contains  * deletions.</p>  *<p>Although per-segment, this file is maintained exterior to compound segment  * files.</p>  *<p>Deletions (.del) --&gt; Format,Header,ByteCount,BitCount, Bits | DGaps (depending  * on Format)</p>  *<ul>  *<li>Format,ByteSize,BitCount --&gt; {@link DataOutput#writeInt Uint32}</li>  *<li>Bits --&gt;&lt;{@link DataOutput#writeByte Byte}&gt;<sup>ByteCount</sup></li>  *<li>DGaps --&gt;&lt;DGap,NonOnesByte&gt;<sup>NonzeroBytesCount</sup></li>  *<li>DGap --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>NonOnesByte --&gt; {@link DataOutput#writeByte Byte}</li>  *<li>Header --&gt; {@link CodecUtil#writeHeader CodecHeader}</li>  *</ul>  *<p>Format is 1: indicates cleared DGaps.</p>  *<p>ByteCount indicates the number of bytes in Bits. It is typically  * (SegSize/8)+1.</p>  *<p>BitCount indicates the number of bits that are currently set in Bits.</p>  *<p>Bits contains one bit for each document indexed. When the bit corresponding  * to a document number is cleared, that document is marked as deleted. Bit ordering  * is from least to most significant. Thus, if Bits contains two bytes, 0x00 and  * 0x02, then document 9 is marked as alive (not deleted).</p>  *<p>DGaps represents sparse bit-vectors more efficiently than Bits. It is made  * of DGaps on indexes of nonOnes bytes in Bits, and the nonOnes bytes themselves.  * The number of nonOnes bytes in Bits (NonOnesBytesCount) is not stored.</p>  *<p>For example, if there are 8000 bits and only bits 10,12,32 are cleared, DGaps  * would be used:</p>  *<p>(VInt) 1 , (byte) 20 , (VInt) 3 , (Byte) 1</p>  */
end_comment

begin_class
DECL|class|Lucene40LiveDocsFormat
specifier|public
class|class
name|Lucene40LiveDocsFormat
extends|extends
name|LiveDocsFormat
block|{
comment|/** Extension of deletes */
DECL|field|DELETES_EXTENSION
specifier|static
specifier|final
name|String
name|DELETES_EXTENSION
init|=
literal|"del"
decl_stmt|;
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|BitVector
name|bitVector
init|=
operator|new
name|BitVector
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|bitVector
operator|.
name|invertAll
argument_list|()
expr_stmt|;
return|return
name|bitVector
return|;
block|}
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|Bits
name|existing
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BitVector
name|liveDocs
init|=
operator|(
name|BitVector
operator|)
name|existing
decl_stmt|;
return|return
name|liveDocs
operator|.
name|clone
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readLiveDocs
specifier|public
name|Bits
name|readLiveDocs
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|filename
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|DELETES_EXTENSION
argument_list|,
name|info
operator|.
name|getDelGen
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BitVector
name|liveDocs
init|=
operator|new
name|BitVector
argument_list|(
name|dir
argument_list|,
name|filename
argument_list|,
name|context
argument_list|)
decl_stmt|;
assert|assert
name|liveDocs
operator|.
name|count
argument_list|()
operator|==
name|info
operator|.
name|docCount
operator|-
name|info
operator|.
name|getDelCount
argument_list|()
operator|:
literal|"liveDocs.count()="
operator|+
name|liveDocs
operator|.
name|count
argument_list|()
operator|+
literal|" info.docCount="
operator|+
name|info
operator|.
name|docCount
operator|+
literal|" info.getDelCount()="
operator|+
name|info
operator|.
name|getDelCount
argument_list|()
assert|;
assert|assert
name|liveDocs
operator|.
name|length
argument_list|()
operator|==
name|info
operator|.
name|docCount
assert|;
return|return
name|liveDocs
return|;
block|}
annotation|@
name|Override
DECL|method|writeLiveDocs
specifier|public
name|void
name|writeLiveDocs
parameter_list|(
name|MutableBits
name|bits
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|int
name|newDelCount
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|filename
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|DELETES_EXTENSION
argument_list|,
name|info
operator|.
name|getNextDelGen
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BitVector
name|liveDocs
init|=
operator|(
name|BitVector
operator|)
name|bits
decl_stmt|;
assert|assert
name|liveDocs
operator|.
name|count
argument_list|()
operator|==
name|info
operator|.
name|docCount
operator|-
name|info
operator|.
name|getDelCount
argument_list|()
operator|-
name|newDelCount
assert|;
assert|assert
name|liveDocs
operator|.
name|length
argument_list|()
operator|==
name|info
operator|.
name|docCount
assert|;
name|liveDocs
operator|.
name|write
argument_list|(
name|dir
argument_list|,
name|filename
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|info
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|DELETES_EXTENSION
argument_list|,
name|info
operator|.
name|getDelGen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


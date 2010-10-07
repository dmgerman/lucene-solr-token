begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|File
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
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
name|MergeScheduler
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
name|ConcurrentMergeScheduler
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
name|CheckIndex
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
name|codecs
operator|.
name|CodecProvider
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
name|codecs
operator|.
name|Codec
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
name|SegmentWriteState
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
name|Directory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_class
DECL|class|_TestUtil
specifier|public
class|class
name|_TestUtil
block|{
comment|/** Returns temp dir, containing String arg in its name;    *  does not create the directory. */
DECL|method|getTempDir
specifier|public
specifier|static
name|File
name|getTempDir
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|LuceneTestCase
operator|.
name|TEMP_DIR
argument_list|,
name|desc
operator|+
literal|"."
operator|+
operator|new
name|Random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
return|;
block|}
DECL|method|rmDir
specifier|public
specifier|static
name|void
name|rmDir
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|f
range|:
name|dir
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|rmDir
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"could not delete "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|dir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"could not delete "
operator|+
name|dir
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|syncConcurrentMerges
specifier|public
specifier|static
name|void
name|syncConcurrentMerges
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|syncConcurrentMerges
argument_list|(
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|syncConcurrentMerges
specifier|public
specifier|static
name|void
name|syncConcurrentMerges
parameter_list|(
name|MergeScheduler
name|ms
parameter_list|)
block|{
if|if
condition|(
name|ms
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/** This runs the CheckIndex tool on the index in.  If any    *  issues are hit, a RuntimeException is thrown; else,    *  true is returned. */
DECL|method|checkIndex
specifier|public
specifier|static
name|CheckIndex
operator|.
name|Status
name|checkIndex
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|CheckIndex
name|checker
init|=
operator|new
name|CheckIndex
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|checker
operator|.
name|setInfoStream
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bos
argument_list|)
argument_list|)
expr_stmt|;
name|CheckIndex
operator|.
name|Status
name|indexStatus
init|=
name|checker
operator|.
name|checkIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexStatus
operator|==
literal|null
operator|||
name|indexStatus
operator|.
name|clean
operator|==
literal|false
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CheckIndex failed"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|bos
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"CheckIndex failed"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|indexStatus
return|;
block|}
block|}
comment|/** start and end are BOTH inclusive */
DECL|method|nextInt
specifier|public
specifier|static
name|int
name|nextInt
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
name|start
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|end
operator|-
name|start
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/** Returns random string, including full unicode range. */
DECL|method|randomUnicodeString
specifier|public
specifier|static
name|String
name|randomUnicodeString
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
return|return
name|randomUnicodeString
argument_list|(
name|r
argument_list|,
literal|20
argument_list|)
return|;
block|}
DECL|method|randomUnicodeString
specifier|public
specifier|static
name|String
name|randomUnicodeString
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|maxLength
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|==
literal|0
condition|)
block|{
comment|// allow 0 length
return|return
literal|""
return|;
block|}
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|end
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
name|end
condition|;
name|i
operator|++
control|)
block|{
name|int
name|t
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|t
operator|&&
name|i
operator|<
name|end
operator|-
literal|1
condition|)
block|{
comment|// Make a surrogate pair
comment|// High surrogate
name|buffer
index|[
name|i
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0xd800
argument_list|,
literal|0xdbff
argument_list|)
expr_stmt|;
comment|// Low surrogate
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0xdc00
argument_list|,
literal|0xdfff
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|<=
literal|1
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|r
operator|.
name|nextInt
argument_list|(
literal|0x80
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|2
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0x80
argument_list|,
literal|0x800
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|3
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0x800
argument_list|,
literal|0xd7ff
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|4
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0xe000
argument_list|,
literal|0xffff
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|end
argument_list|)
return|;
block|}
DECL|field|blockStarts
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|blockStarts
init|=
block|{
literal|0x0000
block|,
literal|0x0080
block|,
literal|0x0100
block|,
literal|0x0180
block|,
literal|0x0250
block|,
literal|0x02B0
block|,
literal|0x0300
block|,
literal|0x0370
block|,
literal|0x0400
block|,
literal|0x0500
block|,
literal|0x0530
block|,
literal|0x0590
block|,
literal|0x0600
block|,
literal|0x0700
block|,
literal|0x0750
block|,
literal|0x0780
block|,
literal|0x07C0
block|,
literal|0x0800
block|,
literal|0x0900
block|,
literal|0x0980
block|,
literal|0x0A00
block|,
literal|0x0A80
block|,
literal|0x0B00
block|,
literal|0x0B80
block|,
literal|0x0C00
block|,
literal|0x0C80
block|,
literal|0x0D00
block|,
literal|0x0D80
block|,
literal|0x0E00
block|,
literal|0x0E80
block|,
literal|0x0F00
block|,
literal|0x1000
block|,
literal|0x10A0
block|,
literal|0x1100
block|,
literal|0x1200
block|,
literal|0x1380
block|,
literal|0x13A0
block|,
literal|0x1400
block|,
literal|0x1680
block|,
literal|0x16A0
block|,
literal|0x1700
block|,
literal|0x1720
block|,
literal|0x1740
block|,
literal|0x1760
block|,
literal|0x1780
block|,
literal|0x1800
block|,
literal|0x18B0
block|,
literal|0x1900
block|,
literal|0x1950
block|,
literal|0x1980
block|,
literal|0x19E0
block|,
literal|0x1A00
block|,
literal|0x1A20
block|,
literal|0x1B00
block|,
literal|0x1B80
block|,
literal|0x1C00
block|,
literal|0x1C50
block|,
literal|0x1CD0
block|,
literal|0x1D00
block|,
literal|0x1D80
block|,
literal|0x1DC0
block|,
literal|0x1E00
block|,
literal|0x1F00
block|,
literal|0x2000
block|,
literal|0x2070
block|,
literal|0x20A0
block|,
literal|0x20D0
block|,
literal|0x2100
block|,
literal|0x2150
block|,
literal|0x2190
block|,
literal|0x2200
block|,
literal|0x2300
block|,
literal|0x2400
block|,
literal|0x2440
block|,
literal|0x2460
block|,
literal|0x2500
block|,
literal|0x2580
block|,
literal|0x25A0
block|,
literal|0x2600
block|,
literal|0x2700
block|,
literal|0x27C0
block|,
literal|0x27F0
block|,
literal|0x2800
block|,
literal|0x2900
block|,
literal|0x2980
block|,
literal|0x2A00
block|,
literal|0x2B00
block|,
literal|0x2C00
block|,
literal|0x2C60
block|,
literal|0x2C80
block|,
literal|0x2D00
block|,
literal|0x2D30
block|,
literal|0x2D80
block|,
literal|0x2DE0
block|,
literal|0x2E00
block|,
literal|0x2E80
block|,
literal|0x2F00
block|,
literal|0x2FF0
block|,
literal|0x3000
block|,
literal|0x3040
block|,
literal|0x30A0
block|,
literal|0x3100
block|,
literal|0x3130
block|,
literal|0x3190
block|,
literal|0x31A0
block|,
literal|0x31C0
block|,
literal|0x31F0
block|,
literal|0x3200
block|,
literal|0x3300
block|,
literal|0x3400
block|,
literal|0x4DC0
block|,
literal|0x4E00
block|,
literal|0xA000
block|,
literal|0xA490
block|,
literal|0xA4D0
block|,
literal|0xA500
block|,
literal|0xA640
block|,
literal|0xA6A0
block|,
literal|0xA700
block|,
literal|0xA720
block|,
literal|0xA800
block|,
literal|0xA830
block|,
literal|0xA840
block|,
literal|0xA880
block|,
literal|0xA8E0
block|,
literal|0xA900
block|,
literal|0xA930
block|,
literal|0xA960
block|,
literal|0xA980
block|,
literal|0xAA00
block|,
literal|0xAA60
block|,
literal|0xAA80
block|,
literal|0xABC0
block|,
literal|0xAC00
block|,
literal|0xD7B0
block|,
literal|0xE000
block|,
literal|0xF900
block|,
literal|0xFB00
block|,
literal|0xFB50
block|,
literal|0xFE00
block|,
literal|0xFE10
block|,
literal|0xFE20
block|,
literal|0xFE30
block|,
literal|0xFE50
block|,
literal|0xFE70
block|,
literal|0xFF00
block|,
literal|0xFFF0
block|,
literal|0x10000
block|,
literal|0x10080
block|,
literal|0x10100
block|,
literal|0x10140
block|,
literal|0x10190
block|,
literal|0x101D0
block|,
literal|0x10280
block|,
literal|0x102A0
block|,
literal|0x10300
block|,
literal|0x10330
block|,
literal|0x10380
block|,
literal|0x103A0
block|,
literal|0x10400
block|,
literal|0x10450
block|,
literal|0x10480
block|,
literal|0x10800
block|,
literal|0x10840
block|,
literal|0x10900
block|,
literal|0x10920
block|,
literal|0x10A00
block|,
literal|0x10A60
block|,
literal|0x10B00
block|,
literal|0x10B40
block|,
literal|0x10B60
block|,
literal|0x10C00
block|,
literal|0x10E60
block|,
literal|0x11080
block|,
literal|0x12000
block|,
literal|0x12400
block|,
literal|0x13000
block|,
literal|0x1D000
block|,
literal|0x1D100
block|,
literal|0x1D200
block|,
literal|0x1D300
block|,
literal|0x1D360
block|,
literal|0x1D400
block|,
literal|0x1F000
block|,
literal|0x1F030
block|,
literal|0x1F100
block|,
literal|0x1F200
block|,
literal|0x20000
block|,
literal|0x2A700
block|,
literal|0x2F800
block|,
literal|0xE0000
block|,
literal|0xE0100
block|,
literal|0xF0000
block|,
literal|0x100000
block|}
decl_stmt|;
DECL|field|blockEnds
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|blockEnds
init|=
block|{
literal|0x007F
block|,
literal|0x00FF
block|,
literal|0x017F
block|,
literal|0x024F
block|,
literal|0x02AF
block|,
literal|0x02FF
block|,
literal|0x036F
block|,
literal|0x03FF
block|,
literal|0x04FF
block|,
literal|0x052F
block|,
literal|0x058F
block|,
literal|0x05FF
block|,
literal|0x06FF
block|,
literal|0x074F
block|,
literal|0x077F
block|,
literal|0x07BF
block|,
literal|0x07FF
block|,
literal|0x083F
block|,
literal|0x097F
block|,
literal|0x09FF
block|,
literal|0x0A7F
block|,
literal|0x0AFF
block|,
literal|0x0B7F
block|,
literal|0x0BFF
block|,
literal|0x0C7F
block|,
literal|0x0CFF
block|,
literal|0x0D7F
block|,
literal|0x0DFF
block|,
literal|0x0E7F
block|,
literal|0x0EFF
block|,
literal|0x0FFF
block|,
literal|0x109F
block|,
literal|0x10FF
block|,
literal|0x11FF
block|,
literal|0x137F
block|,
literal|0x139F
block|,
literal|0x13FF
block|,
literal|0x167F
block|,
literal|0x169F
block|,
literal|0x16FF
block|,
literal|0x171F
block|,
literal|0x173F
block|,
literal|0x175F
block|,
literal|0x177F
block|,
literal|0x17FF
block|,
literal|0x18AF
block|,
literal|0x18FF
block|,
literal|0x194F
block|,
literal|0x197F
block|,
literal|0x19DF
block|,
literal|0x19FF
block|,
literal|0x1A1F
block|,
literal|0x1AAF
block|,
literal|0x1B7F
block|,
literal|0x1BBF
block|,
literal|0x1C4F
block|,
literal|0x1C7F
block|,
literal|0x1CFF
block|,
literal|0x1D7F
block|,
literal|0x1DBF
block|,
literal|0x1DFF
block|,
literal|0x1EFF
block|,
literal|0x1FFF
block|,
literal|0x206F
block|,
literal|0x209F
block|,
literal|0x20CF
block|,
literal|0x20FF
block|,
literal|0x214F
block|,
literal|0x218F
block|,
literal|0x21FF
block|,
literal|0x22FF
block|,
literal|0x23FF
block|,
literal|0x243F
block|,
literal|0x245F
block|,
literal|0x24FF
block|,
literal|0x257F
block|,
literal|0x259F
block|,
literal|0x25FF
block|,
literal|0x26FF
block|,
literal|0x27BF
block|,
literal|0x27EF
block|,
literal|0x27FF
block|,
literal|0x28FF
block|,
literal|0x297F
block|,
literal|0x29FF
block|,
literal|0x2AFF
block|,
literal|0x2BFF
block|,
literal|0x2C5F
block|,
literal|0x2C7F
block|,
literal|0x2CFF
block|,
literal|0x2D2F
block|,
literal|0x2D7F
block|,
literal|0x2DDF
block|,
literal|0x2DFF
block|,
literal|0x2E7F
block|,
literal|0x2EFF
block|,
literal|0x2FDF
block|,
literal|0x2FFF
block|,
literal|0x303F
block|,
literal|0x309F
block|,
literal|0x30FF
block|,
literal|0x312F
block|,
literal|0x318F
block|,
literal|0x319F
block|,
literal|0x31BF
block|,
literal|0x31EF
block|,
literal|0x31FF
block|,
literal|0x32FF
block|,
literal|0x33FF
block|,
literal|0x4DBF
block|,
literal|0x4DFF
block|,
literal|0x9FFF
block|,
literal|0xA48F
block|,
literal|0xA4CF
block|,
literal|0xA4FF
block|,
literal|0xA63F
block|,
literal|0xA69F
block|,
literal|0xA6FF
block|,
literal|0xA71F
block|,
literal|0xA7FF
block|,
literal|0xA82F
block|,
literal|0xA83F
block|,
literal|0xA87F
block|,
literal|0xA8DF
block|,
literal|0xA8FF
block|,
literal|0xA92F
block|,
literal|0xA95F
block|,
literal|0xA97F
block|,
literal|0xA9DF
block|,
literal|0xAA5F
block|,
literal|0xAA7F
block|,
literal|0xAADF
block|,
literal|0xABFF
block|,
literal|0xD7AF
block|,
literal|0xD7FF
block|,
literal|0xF8FF
block|,
literal|0xFAFF
block|,
literal|0xFB4F
block|,
literal|0xFDFF
block|,
literal|0xFE0F
block|,
literal|0xFE1F
block|,
literal|0xFE2F
block|,
literal|0xFE4F
block|,
literal|0xFE6F
block|,
literal|0xFEFF
block|,
literal|0xFFEF
block|,
literal|0xFFFF
block|,
literal|0x1007F
block|,
literal|0x100FF
block|,
literal|0x1013F
block|,
literal|0x1018F
block|,
literal|0x101CF
block|,
literal|0x101FF
block|,
literal|0x1029F
block|,
literal|0x102DF
block|,
literal|0x1032F
block|,
literal|0x1034F
block|,
literal|0x1039F
block|,
literal|0x103DF
block|,
literal|0x1044F
block|,
literal|0x1047F
block|,
literal|0x104AF
block|,
literal|0x1083F
block|,
literal|0x1085F
block|,
literal|0x1091F
block|,
literal|0x1093F
block|,
literal|0x10A5F
block|,
literal|0x10A7F
block|,
literal|0x10B3F
block|,
literal|0x10B5F
block|,
literal|0x10B7F
block|,
literal|0x10C4F
block|,
literal|0x10E7F
block|,
literal|0x110CF
block|,
literal|0x123FF
block|,
literal|0x1247F
block|,
literal|0x1342F
block|,
literal|0x1D0FF
block|,
literal|0x1D1FF
block|,
literal|0x1D24F
block|,
literal|0x1D35F
block|,
literal|0x1D37F
block|,
literal|0x1D7FF
block|,
literal|0x1F02F
block|,
literal|0x1F09F
block|,
literal|0x1F1FF
block|,
literal|0x1F2FF
block|,
literal|0x2A6DF
block|,
literal|0x2B73F
block|,
literal|0x2FA1F
block|,
literal|0xE007F
block|,
literal|0xE01EF
block|,
literal|0xFFFFF
block|,
literal|0x10FFFF
block|}
decl_stmt|;
comment|/** Returns random string, all codepoints within the same unicode block. */
DECL|method|randomRealisticUnicodeString
specifier|public
specifier|static
name|String
name|randomRealisticUnicodeString
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
return|return
name|randomRealisticUnicodeString
argument_list|(
name|r
argument_list|,
literal|20
argument_list|)
return|;
block|}
comment|/** Returns random string, all codepoints within the same unicode block. */
DECL|method|randomRealisticUnicodeString
specifier|public
specifier|static
name|String
name|randomRealisticUnicodeString
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|maxLength
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxLength
argument_list|)
decl_stmt|;
specifier|final
name|int
name|block
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|blockStarts
operator|.
name|length
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|end
condition|;
name|i
operator|++
control|)
name|sb
operator|.
name|appendCodePoint
argument_list|(
name|nextInt
argument_list|(
name|r
argument_list|,
name|blockStarts
index|[
name|block
index|]
argument_list|,
name|blockEnds
index|[
name|block
index|]
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|alwaysCodec
specifier|public
specifier|static
name|CodecProvider
name|alwaysCodec
parameter_list|(
specifier|final
name|Codec
name|c
parameter_list|)
block|{
return|return
operator|new
name|CodecProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Codec
name|getWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
block|{
return|return
name|c
return|;
block|}
annotation|@
name|Override
specifier|public
name|Codec
name|lookup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// can't do this until we fix PreFlexRW to not
comment|//impersonate PreFlex:
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|c
operator|.
name|name
argument_list|)
condition|)
block|{
return|return
name|c
return|;
block|}
else|else
block|{
return|return
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
comment|/** Return a CodecProvider that can read any of the    *  default codecs, but always writes in the specified    *  codec. */
DECL|method|alwaysCodec
specifier|public
specifier|static
name|CodecProvider
name|alwaysCodec
parameter_list|(
specifier|final
name|String
name|codec
parameter_list|)
block|{
return|return
name|alwaysCodec
argument_list|(
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|lookup
argument_list|(
name|codec
argument_list|)
argument_list|)
return|;
block|}
DECL|method|anyFilesExceptWriteLock
specifier|public
specifier|static
name|boolean
name|anyFilesExceptWriteLock
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|listAll
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|length
operator|>
literal|1
operator|||
operator|(
name|files
operator|.
name|length
operator|==
literal|1
operator|&&
operator|!
name|files
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"write.lock"
argument_list|)
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package

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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteOrder
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
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|AnalyzerProfile
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
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|Utility
import|;
end_import

begin_class
DECL|class|CopyOfWordDictionary
specifier|public
class|class
name|CopyOfWordDictionary
extends|extends
name|AbstractDictionary
block|{
DECL|method|CopyOfWordDictionary
specifier|private
name|CopyOfWordDictionary
parameter_list|()
block|{   }
DECL|field|singleInstance
specifier|private
specifier|static
name|CopyOfWordDictionary
name|singleInstance
decl_stmt|;
comment|/**    * ä¸ä¸ªè¾å¤§çç´ æ°ï¼ä¿è¯hashæ¥æ¾è½å¤éåææä½ç½®    */
DECL|field|PRIME_INDEX_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|PRIME_INDEX_LENGTH
init|=
literal|12071
decl_stmt|;
comment|/**    * wordIndexTableä¿è¯å°Unicodeä¸­çæææ±å­ç¼ç hashå°PRIME_INDEX_LENGTHé¿åº¦çæ°ç»ä¸­ï¼    * å½ç¶ä¼æå²çªï¼ä½å®éä¸æ¬ç¨åºåªå¤çGB2312å­ç¬¦é¨åï¼6768ä¸ªå­ç¬¦å ä¸ä¸äºASCIIå­ç¬¦ï¼    * å æ­¤å¯¹è¿äºå­ç¬¦æ¯ææçï¼ä¸ºäºä¿è¯æ¯è¾çåç¡®æ§ï¼ä¿çåæ¥çå­ç¬¦å¨charIndexTableä¸­ä»¥ç¡®å®æ¥æ¾çåç¡®æ§    */
DECL|field|wordIndexTable
specifier|private
name|short
index|[]
name|wordIndexTable
decl_stmt|;
DECL|field|charIndexTable
specifier|private
name|char
index|[]
name|charIndexTable
decl_stmt|;
comment|/**    * å­å¨ææè¯åºççæ­£æ°æ®ç»æï¼ä¸ºäºé¿åå ç¨ç©ºé´å¤ªå¤ï¼ç¨äºä¸¤ä¸ªåç¬çå¤ç»´æ°ç»æ¥å­å¨è¯ç»åé¢çã    * æ¯ä¸ªè¯æ¾å¨ä¸ä¸ªchar[]ä¸­ï¼æ¯ä¸ªcharå¯¹åºä¸ä¸ªæ±å­æå¶ä»å­ç¬¦ï¼æ¯ä¸ªé¢çæ¾å¨ä¸ä¸ªintä¸­ï¼    * è¿ä¸¤ä¸ªæ°ç»çåä¸¤ä¸ªä¸è¡¨æ¯ä¸ä¸å¯¹åºçãå æ­¤å¯ä»¥å©ç¨wordItem_charArrayTable[i][j]æ¥æ¥è¯ï¼    * ç¨wordItem_frequencyTable[i][j]æ¥æ¥è¯¢å¯¹åºçé¢ç    */
DECL|field|wordItem_charArrayTable
specifier|private
name|char
index|[]
index|[]
index|[]
name|wordItem_charArrayTable
decl_stmt|;
DECL|field|wordItem_frequencyTable
specifier|private
name|int
index|[]
index|[]
name|wordItem_frequencyTable
decl_stmt|;
comment|// static Logger log = Logger.getLogger(WordDictionary.class);
DECL|method|getInstance
specifier|public
specifier|synchronized
specifier|static
name|CopyOfWordDictionary
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|singleInstance
operator|==
literal|null
condition|)
block|{
name|singleInstance
operator|=
operator|new
name|CopyOfWordDictionary
argument_list|()
expr_stmt|;
name|String
name|wordDictRoot
init|=
name|AnalyzerProfile
operator|.
name|ANALYSIS_DATA_DIR
decl_stmt|;
name|singleInstance
operator|.
name|load
argument_list|(
name|wordDictRoot
argument_list|)
expr_stmt|;
block|}
return|return
name|singleInstance
return|;
block|}
comment|/**    * å å¨è¯å¸åºæä»¶ï¼    *     * @param dctFileName è¯å¸åºæä»¶çè·¯å¾    */
DECL|method|load
specifier|public
name|void
name|load
parameter_list|(
name|String
name|dctFileRoot
parameter_list|)
block|{
name|String
name|dctFilePath
init|=
name|dctFileRoot
operator|+
literal|"/coredict.dct"
decl_stmt|;
name|File
name|serialObj
init|=
operator|new
name|File
argument_list|(
name|dctFileRoot
operator|+
literal|"/coredict.mem"
argument_list|)
decl_stmt|;
if|if
condition|(
name|serialObj
operator|.
name|exists
argument_list|()
operator|&&
name|loadFromObj
argument_list|(
name|serialObj
argument_list|)
condition|)
block|{      }
else|else
block|{
try|try
block|{
name|wordIndexTable
operator|=
operator|new
name|short
index|[
name|PRIME_INDEX_LENGTH
index|]
expr_stmt|;
name|charIndexTable
operator|=
operator|new
name|char
index|[
name|PRIME_INDEX_LENGTH
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PRIME_INDEX_LENGTH
condition|;
name|i
operator|++
control|)
block|{
name|charIndexTable
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
name|wordIndexTable
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|wordItem_charArrayTable
operator|=
operator|new
name|char
index|[
name|GB2312_CHAR_NUM
index|]
index|[]
index|[]
expr_stmt|;
name|wordItem_frequencyTable
operator|=
operator|new
name|int
index|[
name|GB2312_CHAR_NUM
index|]
index|[]
expr_stmt|;
comment|// int total =
name|loadMainDataFromFile
argument_list|(
name|dctFilePath
argument_list|)
expr_stmt|;
name|expandDelimiterData
argument_list|()
expr_stmt|;
name|mergeSameWords
argument_list|()
expr_stmt|;
name|sortEachItems
argument_list|()
expr_stmt|;
comment|// log.info("load dictionary: " + dctFilePath + " total:" + total);
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|saveToObj
argument_list|(
name|serialObj
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadFromObj
specifier|private
name|boolean
name|loadFromObj
parameter_list|(
name|File
name|serialObj
parameter_list|)
block|{
name|boolean
name|loadFromObject
init|=
literal|false
decl_stmt|;
try|try
block|{
name|ObjectInputStream
name|input
init|=
operator|new
name|ObjectInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|serialObj
argument_list|)
argument_list|)
decl_stmt|;
name|wordIndexTable
operator|=
operator|(
name|short
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|charIndexTable
operator|=
operator|(
name|char
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|wordItem_charArrayTable
operator|=
operator|(
name|char
index|[]
index|[]
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|wordItem_frequencyTable
operator|=
operator|(
name|int
index|[]
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
comment|// log.info("load core dict from serialization.");
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|loadFromObject
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// log.warn(e.getMessage());
block|}
return|return
name|loadFromObject
return|;
block|}
DECL|method|saveToObj
specifier|private
name|void
name|saveToObj
parameter_list|(
name|File
name|serialObj
parameter_list|)
block|{
try|try
block|{
name|ObjectOutputStream
name|output
init|=
operator|new
name|ObjectOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|serialObj
argument_list|)
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|wordIndexTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|charIndexTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|wordItem_charArrayTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|wordItem_frequencyTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// log.info("serialize core dict.");
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// log.warn(e.getMessage());
block|}
block|}
comment|/**    * å°è¯åºæä»¶å è½½å°WordDictionaryçç¸å³æ°æ®ç»æä¸­ï¼åªæ¯å è½½ï¼æ²¡æè¿è¡åå¹¶åä¿®æ¹æä½    *     * @param dctFilePath    * @return    * @throws FileNotFoundException    * @throws IOException    * @throws UnsupportedEncodingException    */
DECL|method|loadMainDataFromFile
specifier|private
name|int
name|loadMainDataFromFile
parameter_list|(
name|String
name|dctFilePath
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
throws|,
name|UnsupportedEncodingException
block|{
name|int
name|i
decl_stmt|,
name|cnt
decl_stmt|,
name|length
decl_stmt|,
name|total
init|=
literal|0
decl_stmt|;
comment|// æä»¶ä¸­åªç»è®¡äº6763ä¸ªæ±å­å 5ä¸ªç©ºæ±å­ç¬¦3756~3760ï¼å¶ä¸­ç¬¬3756ä¸ªç¨æ¥å­å¨ç¬¦å·ä¿¡æ¯ã
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
literal|3
index|]
decl_stmt|;
name|byte
index|[]
name|intBuffer
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|String
name|tmpword
decl_stmt|;
name|RandomAccessFile
name|dctFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|dctFilePath
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
comment|// å­å¸æä»¶ä¸­ç¬¬ä¸ä¸ªæ±å­åºç°çä½ç½®æ¯0ï¼æåä¸ä¸ªæ¯6768
for|for
control|(
name|i
operator|=
name|GB2312_FIRST_CHAR
init|;
name|i
operator|<
name|GB2312_FIRST_CHAR
operator|+
name|CHAR_NUM_IN_FILE
condition|;
name|i
operator|++
control|)
block|{
comment|// if (i == 5231)
comment|// System.out.println(i);
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
comment|// åè¯åºæä»¶å¨cä¸å¼åï¼æä»¥åå¥çæä»¶ä¸ºlittle
comment|// endianç¼ç ï¼èjavaä¸ºbig endianï¼å¿é¡»è½¬æ¢è¿æ¥
name|cnt
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|cnt
operator|<=
literal|0
condition|)
block|{
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
continue|continue;
block|}
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|=
operator|new
name|char
index|[
name|cnt
index|]
index|[]
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|cnt
index|]
expr_stmt|;
name|total
operator|+=
name|cnt
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|cnt
condition|)
block|{
comment|// wordItemTable[i][j] = new WordItem();
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
comment|// frequency
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|1
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
comment|// length
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|2
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
comment|// handle
comment|// wordItemTable[i][j].frequency = buffer[0];
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|buffer
index|[
literal|0
index|]
expr_stmt|;
name|length
operator|=
name|buffer
index|[
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|lchBuffer
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|dctFile
operator|.
name|read
argument_list|(
name|lchBuffer
argument_list|)
expr_stmt|;
name|tmpword
operator|=
operator|new
name|String
argument_list|(
name|lchBuffer
argument_list|,
literal|"GB2312"
argument_list|)
expr_stmt|;
comment|// indexTable[i].wordItems[j].word = tmpword;
comment|// wordItemTable[i][j].charArray = tmpword.toCharArray();
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|tmpword
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// wordItemTable[i][j].charArray = null;
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
block|}
comment|// System.out.println(indexTable[i].wordItems[j]);
name|j
operator|++
expr_stmt|;
block|}
name|String
name|str
init|=
name|getCCByGB2312Id
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|setTableIndex
argument_list|(
name|str
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|dctFile
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|total
return|;
block|}
comment|/**    * åè¯åºå°æææ ç¹ç¬¦å·çä¿¡æ¯åå¹¶å°ä¸ä¸ªåè¡¨é(ä»1å¼å§ç3755å¤)ãè¿éå°å¶å±å¼ï¼åå«æ¾å°åä¸ªç¬¦å·å¯¹åºçåè¡¨ä¸­    */
DECL|method|expandDelimiterData
specifier|private
name|void
name|expandDelimiterData
parameter_list|()
block|{
name|int
name|i
decl_stmt|;
name|int
name|cnt
decl_stmt|;
comment|// æ ç¹ç¬¦å·å¨ä»1å¼å§ç3755å¤ï¼å°åå§çæ ç¹ç¬¦å·å¯¹åºçå­å¸åéå°å¯¹åºçæ ç¹ç¬¦å·ä¸­
name|int
name|delimiterIndex
init|=
literal|3755
operator|+
name|GB2312_FIRST_CHAR
decl_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|i
operator|<
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
operator|.
name|length
condition|)
block|{
name|char
name|c
init|=
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
index|[
name|i
index|]
index|[
literal|0
index|]
decl_stmt|;
name|int
name|j
init|=
name|getGB2312Id
argument_list|(
name|c
argument_list|)
decl_stmt|;
comment|// è¯¥æ ç¹ç¬¦å·åºè¯¥æå¨çindexå¼
if|if
condition|(
name|wordItem_charArrayTable
index|[
name|j
index|]
operator|==
literal|null
condition|)
block|{
name|int
name|k
init|=
name|i
decl_stmt|;
comment|// ä»iå¼å§è®¡æ°åé¢ä»¥jå¼å¤´çç¬¦å·çworditemçä¸ªæ°
while|while
condition|(
name|k
operator|<
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
operator|.
name|length
operator|&&
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
index|[
name|k
index|]
index|[
literal|0
index|]
operator|==
name|c
condition|)
block|{
name|k
operator|++
expr_stmt|;
block|}
comment|// æ­¤æ¶k-iä¸ºidä¸ºjçæ ç¹ç¬¦å·å¯¹åºçwordItemçä¸ªæ°
name|cnt
operator|=
name|k
operator|-
name|i
expr_stmt|;
if|if
condition|(
name|cnt
operator|!=
literal|0
condition|)
block|{
name|wordItem_charArrayTable
index|[
name|j
index|]
operator|=
operator|new
name|char
index|[
name|cnt
index|]
index|[]
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|j
index|]
operator|=
operator|new
name|int
index|[
name|cnt
index|]
expr_stmt|;
block|}
comment|// ä¸ºæ¯ä¸ä¸ªwordItemèµå¼
for|for
control|(
name|k
operator|=
literal|0
init|;
name|k
operator|<
name|cnt
condition|;
name|k
operator|++
operator|,
name|i
operator|++
control|)
block|{
comment|// wordItemTable[j][k] = new WordItem();
name|wordItem_frequencyTable
index|[
name|j
index|]
index|[
name|k
index|]
operator|=
name|wordItem_frequencyTable
index|[
name|delimiterIndex
index|]
index|[
name|i
index|]
expr_stmt|;
name|wordItem_charArrayTable
index|[
name|j
index|]
index|[
name|k
index|]
operator|=
operator|new
name|char
index|[
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
index|[
name|i
index|]
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
index|[
name|i
index|]
argument_list|,
literal|1
argument_list|,
name|wordItem_charArrayTable
index|[
name|j
index|]
index|[
name|k
index|]
argument_list|,
literal|0
argument_list|,
name|wordItem_charArrayTable
index|[
name|j
index|]
index|[
name|k
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|setTableIndex
argument_list|(
name|c
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
block|}
comment|// å°åç¬¦å·å¯¹åºçæ°ç»å é¤
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
operator|=
literal|null
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|delimiterIndex
index|]
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * æ¬ç¨åºä¸åè¯æ§æ æ³¨ï¼å æ­¤å°ç¸åè¯ä¸åè¯æ§çé¢çåå¹¶å°åä¸ä¸ªè¯ä¸ï¼ä»¥åå°å­å¨ç©ºé´ï¼å å¿«æç´¢éåº¦    */
DECL|method|mergeSameWords
specifier|private
name|void
name|mergeSameWords
parameter_list|()
block|{
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|GB2312_FIRST_CHAR
operator|+
name|CHAR_NUM_IN_FILE
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|==
literal|null
condition|)
continue|continue;
name|int
name|len
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|Utility
operator|.
name|compareArray
argument_list|(
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|,
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
operator|-
literal|1
index|]
argument_list|,
literal|0
argument_list|)
operator|!=
literal|0
condition|)
name|len
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
condition|)
block|{
name|char
index|[]
index|[]
name|tempArray
init|=
operator|new
name|char
index|[
name|len
index|]
index|[]
decl_stmt|;
name|int
index|[]
name|tempFreq
init|=
operator|new
name|int
index|[
name|len
index|]
decl_stmt|;
name|int
name|k
init|=
literal|0
decl_stmt|;
name|tempArray
index|[
literal|0
index|]
operator|=
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
literal|0
index|]
expr_stmt|;
name|tempFreq
index|[
literal|0
index|]
operator|=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
literal|0
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|Utility
operator|.
name|compareArray
argument_list|(
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|,
name|tempArray
index|[
name|k
index|]
argument_list|,
literal|0
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|k
operator|++
expr_stmt|;
comment|// temp[k] = wordItemTable[i][j];
name|tempArray
index|[
name|k
index|]
operator|=
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
name|tempFreq
index|[
name|k
index|]
operator|=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// temp[k].frequency += wordItemTable[i][j].frequency;
name|tempFreq
index|[
name|k
index|]
operator|+=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
comment|// wordItemTable[i] = temp;
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|=
name|tempArray
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
operator|=
name|tempFreq
expr_stmt|;
block|}
block|}
block|}
DECL|method|sortEachItems
specifier|private
name|void
name|sortEachItems
parameter_list|()
block|{
name|char
index|[]
name|tmpArray
decl_stmt|;
name|int
name|tmpFreq
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
name|wordItem_charArrayTable
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|int
name|j2
init|=
name|j
operator|+
literal|1
init|;
name|j2
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j2
operator|++
control|)
block|{
if|if
condition|(
name|Utility
operator|.
name|compareArray
argument_list|(
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|,
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j2
index|]
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
condition|)
block|{
name|tmpArray
operator|=
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
name|tmpFreq
operator|=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j2
index|]
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j2
index|]
expr_stmt|;
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j2
index|]
operator|=
name|tmpArray
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j2
index|]
operator|=
name|tmpFreq
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/**    * è®¡ç®å­ç¬¦cå¨åå¸è¡¨ä¸­åºè¯¥å¨çä½ç½®ï¼ç¶åå°å°ååè¡¨ä¸­è¯¥ä½ç½®çå¼åå§å    *     * @param c    * @param j    * @return    */
DECL|method|setTableIndex
specifier|private
name|boolean
name|setTableIndex
parameter_list|(
name|char
name|c
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|index
init|=
name|getAvaliableTableIndex
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|charIndexTable
index|[
name|index
index|]
operator|=
name|c
expr_stmt|;
name|wordIndexTable
index|[
name|index
index|]
operator|=
operator|(
name|short
operator|)
name|j
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
DECL|method|getAvaliableTableIndex
specifier|private
name|short
name|getAvaliableTableIndex
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|int
name|hash1
init|=
call|(
name|int
call|)
argument_list|(
name|hash1
argument_list|(
name|c
argument_list|)
operator|%
name|PRIME_INDEX_LENGTH
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|hash2
argument_list|(
name|c
argument_list|)
operator|%
name|PRIME_INDEX_LENGTH
decl_stmt|;
if|if
condition|(
name|hash1
operator|<
literal|0
condition|)
name|hash1
operator|=
name|PRIME_INDEX_LENGTH
operator|+
name|hash1
expr_stmt|;
if|if
condition|(
name|hash2
operator|<
literal|0
condition|)
name|hash2
operator|=
name|PRIME_INDEX_LENGTH
operator|+
name|hash2
expr_stmt|;
name|int
name|index
init|=
name|hash1
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|charIndexTable
index|[
name|index
index|]
operator|!=
literal|0
operator|&&
name|charIndexTable
index|[
name|index
index|]
operator|!=
name|c
operator|&&
name|i
operator|<
name|PRIME_INDEX_LENGTH
condition|)
block|{
name|index
operator|=
operator|(
name|hash1
operator|+
name|i
operator|*
name|hash2
operator|)
operator|%
name|PRIME_INDEX_LENGTH
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
comment|// System.out.println(i - 1);
if|if
condition|(
name|i
operator|<
name|PRIME_INDEX_LENGTH
operator|&&
operator|(
name|charIndexTable
index|[
name|index
index|]
operator|==
literal|0
operator|||
name|charIndexTable
index|[
name|index
index|]
operator|==
name|c
operator|)
condition|)
block|{
return|return
operator|(
name|short
operator|)
name|index
return|;
block|}
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * @param c    * @return    */
DECL|method|getWordItemTableIndex
specifier|private
name|short
name|getWordItemTableIndex
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|int
name|hash1
init|=
call|(
name|int
call|)
argument_list|(
name|hash1
argument_list|(
name|c
argument_list|)
operator|%
name|PRIME_INDEX_LENGTH
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|hash2
argument_list|(
name|c
argument_list|)
operator|%
name|PRIME_INDEX_LENGTH
decl_stmt|;
if|if
condition|(
name|hash1
operator|<
literal|0
condition|)
name|hash1
operator|=
name|PRIME_INDEX_LENGTH
operator|+
name|hash1
expr_stmt|;
if|if
condition|(
name|hash2
operator|<
literal|0
condition|)
name|hash2
operator|=
name|PRIME_INDEX_LENGTH
operator|+
name|hash2
expr_stmt|;
name|int
name|index
init|=
name|hash1
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|charIndexTable
index|[
name|index
index|]
operator|!=
literal|0
operator|&&
name|charIndexTable
index|[
name|index
index|]
operator|!=
name|c
operator|&&
name|i
operator|<
name|PRIME_INDEX_LENGTH
condition|)
block|{
name|index
operator|=
operator|(
name|hash1
operator|+
name|i
operator|*
name|hash2
operator|)
operator|%
name|PRIME_INDEX_LENGTH
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<
name|PRIME_INDEX_LENGTH
operator|&&
name|charIndexTable
index|[
name|index
index|]
operator|==
name|c
condition|)
block|{
return|return
operator|(
name|short
operator|)
name|index
return|;
block|}
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * å¨å­å¸åºä¸­æ¥æ¾åè¯å¯¹åºçcharæ°ç»ä¸ºcharArrayçå­ç¬¦ä¸²ãè¿åè¯¥åè¯å¨åè¯åºåä¸­çä½ç½®    *     * @param charArray æ¥æ¾åè¯å¯¹åºçcharæ°ç»    * @return åè¯å¨åè¯æ°ç»ä¸­çä½ç½®ï¼å¦ææ²¡æ¾å°åè¿å-1    */
DECL|method|findInTable
specifier|private
name|int
name|findInTable
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|)
block|{
if|if
condition|(
name|charArray
operator|==
literal|null
operator|||
name|charArray
operator|.
name|length
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|short
name|index
init|=
name|getWordItemTableIndex
argument_list|(
name|charArray
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
return|return
operator|-
literal|1
return|;
return|return
name|findInTable
argument_list|(
name|index
argument_list|,
name|charArray
argument_list|)
return|;
block|}
comment|/**    * å¨å­å¸åºä¸­æ¥æ¾åè¯å¯¹åºçcharæ°ç»ä¸ºcharArrayçå­ç¬¦ä¸²ãè¿åè¯¥åè¯å¨åè¯åºåä¸­çä½ç½®    *     * @param knownHashIndex å·²ç¥åè¯ç¬¬ä¸ä¸ªå­ç¬¦charArray[0]å¨hashè¡¨ä¸­çä½ç½®ï¼å¦ææªè®¡ç®ï¼å¯ä»¥ç¨å½æ°int    *        findInTable(char[] charArray) ä»£æ¿    * @param charArray æ¥æ¾åè¯å¯¹åºçcharæ°ç»    * @return åè¯å¨åè¯æ°ç»ä¸­çä½ç½®ï¼å¦ææ²¡æ¾å°åè¿å-1    */
DECL|method|findInTable
specifier|private
name|int
name|findInTable
parameter_list|(
name|short
name|knownHashIndex
parameter_list|,
name|char
index|[]
name|charArray
parameter_list|)
block|{
if|if
condition|(
name|charArray
operator|==
literal|null
operator|||
name|charArray
operator|.
name|length
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|char
index|[]
index|[]
name|items
init|=
name|wordItem_charArrayTable
index|[
name|wordIndexTable
index|[
name|knownHashIndex
index|]
index|]
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|,
name|end
init|=
name|items
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
operator|(
name|start
operator|+
name|end
operator|)
operator|/
literal|2
decl_stmt|,
name|cmpResult
decl_stmt|;
comment|// Binary search for the index of idArray
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|cmpResult
operator|=
name|Utility
operator|.
name|compareArray
argument_list|(
name|items
index|[
name|mid
index|]
argument_list|,
literal|0
argument_list|,
name|charArray
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmpResult
operator|==
literal|0
condition|)
return|return
name|mid
return|;
comment|// find it
elseif|else
if|if
condition|(
name|cmpResult
operator|<
literal|0
condition|)
name|start
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|cmpResult
operator|>
literal|0
condition|)
name|end
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
name|mid
operator|=
operator|(
name|start
operator|+
name|end
operator|)
operator|/
literal|2
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * charArrayè¿ä¸ªåè¯å¯¹åºçè¯ç»å¨ä¸å¨WordDictionaryä¸­åºç°    *     * @param charArray    * @return trueè¡¨ç¤ºå­å¨ï¼falseè¡¨ç¤ºä¸å­å¨    */
DECL|method|isExist
specifier|public
name|boolean
name|isExist
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|)
block|{
return|return
name|findInTable
argument_list|(
name|charArray
argument_list|)
operator|!=
operator|-
literal|1
return|;
block|}
comment|/**    * @see{getPrefixMatch(char[] charArray, int knownStart)}    * @param charArray    * @return    */
DECL|method|getPrefixMatch
specifier|public
name|int
name|getPrefixMatch
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|)
block|{
return|return
name|getPrefixMatch
argument_list|(
name|charArray
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * ä»è¯å¸ä¸­æ¥æ¾ä»¥charArrayå¯¹åºçåè¯ä¸ºåç¼(prefix)çåè¯çä½ç½®, å¹¶è¿åç¬¬ä¸ä¸ªæ»¡è¶³æ¡ä»¶çä½ç½®ãä¸ºäºåå°æç´¢ä»£ä»·,    * å¯ä»¥æ ¹æ®å·²æç¥è¯è®¾ç½®èµ·å§æç´¢ä½ç½®, å¦æä¸ç¥éèµ·å§ä½ç½®ï¼é»è®¤æ¯0    *     * @see{getPrefixMatch(char[] charArray)}    * @param charArray åç¼åè¯    * @param knownStart å·²ç¥çèµ·å§ä½ç½®    * @return æ»¡è¶³åç¼æ¡ä»¶çç¬¬ä¸ä¸ªåè¯çä½ç½®    */
DECL|method|getPrefixMatch
specifier|public
name|int
name|getPrefixMatch
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|,
name|int
name|knownStart
parameter_list|)
block|{
name|short
name|index
init|=
name|getWordItemTableIndex
argument_list|(
name|charArray
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
return|return
operator|-
literal|1
return|;
name|char
index|[]
index|[]
name|items
init|=
name|wordItem_charArrayTable
index|[
name|wordIndexTable
index|[
name|index
index|]
index|]
decl_stmt|;
name|int
name|start
init|=
name|knownStart
decl_stmt|,
name|end
init|=
name|items
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
operator|(
name|start
operator|+
name|end
operator|)
operator|/
literal|2
decl_stmt|,
name|cmpResult
decl_stmt|;
comment|// Binary search for the index of idArray
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|cmpResult
operator|=
name|Utility
operator|.
name|compareArrayByPrefix
argument_list|(
name|charArray
argument_list|,
literal|1
argument_list|,
name|items
index|[
name|mid
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmpResult
operator|==
literal|0
condition|)
block|{
comment|// Get the first item which match the current word
while|while
condition|(
name|mid
operator|>=
literal|0
operator|&&
name|Utility
operator|.
name|compareArrayByPrefix
argument_list|(
name|charArray
argument_list|,
literal|1
argument_list|,
name|items
index|[
name|mid
index|]
argument_list|,
literal|0
argument_list|)
operator|==
literal|0
condition|)
name|mid
operator|--
expr_stmt|;
name|mid
operator|++
expr_stmt|;
return|return
name|mid
return|;
comment|// æ¾å°ç¬¬ä¸ä¸ªä»¥charArrayä¸ºåç¼çåè¯
block|}
elseif|else
if|if
condition|(
name|cmpResult
operator|<
literal|0
condition|)
name|end
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
name|start
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
name|mid
operator|=
operator|(
name|start
operator|+
name|end
operator|)
operator|/
literal|2
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * è·åidArrayå¯¹åºçè¯çè¯é¢ï¼è¥posä¸º-1åè·åææè¯æ§çè¯é¢    *     * @param charArray è¾å¥çåè¯å¯¹åºçcharArray    * @param pos è¯æ§ï¼-1è¡¨ç¤ºè¦æ±æ±åºææçè¯æ§çè¯é¢    * @return idArrayå¯¹åºçè¯é¢    */
DECL|method|getFrequency
specifier|public
name|int
name|getFrequency
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|)
block|{
name|short
name|hashIndex
init|=
name|getWordItemTableIndex
argument_list|(
name|charArray
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|hashIndex
operator|==
operator|-
literal|1
condition|)
return|return
literal|0
return|;
name|int
name|itemIndex
init|=
name|findInTable
argument_list|(
name|hashIndex
argument_list|,
name|charArray
argument_list|)
decl_stmt|;
if|if
condition|(
name|itemIndex
operator|!=
operator|-
literal|1
condition|)
return|return
name|wordItem_frequencyTable
index|[
name|wordIndexTable
index|[
name|hashIndex
index|]
index|]
index|[
name|itemIndex
index|]
return|;
return|return
literal|0
return|;
block|}
comment|/**    * å¤æ­charArrayå¯¹åºçå­ç¬¦ä¸²æ¯å¦è·è¯å¸ä¸­charArray[0]å¯¹åºçwordIndexçcharArrayç¸ç­,    * ä¹å°±æ¯è¯´charArrayçä½ç½®æ¥æ¾ç»ææ¯ä¸æ¯å°±æ¯wordIndex    *     * @param charArray è¾å¥çcharArrayè¯ç»ï¼ç¬¬ä¸ä¸ªæ°è¡¨ç¤ºè¯å¸ä¸­çç´¢å¼å·    * @param itemIndex ä½ç½®ç¼å·    * @return æ¯å¦ç¸ç­    */
DECL|method|isEqual
specifier|public
name|boolean
name|isEqual
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|,
name|int
name|itemIndex
parameter_list|)
block|{
name|short
name|hashIndex
init|=
name|getWordItemTableIndex
argument_list|(
name|charArray
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
return|return
name|Utility
operator|.
name|compareArray
argument_list|(
name|charArray
argument_list|,
literal|1
argument_list|,
name|wordItem_charArrayTable
index|[
name|wordIndexTable
index|[
name|hashIndex
index|]
index|]
index|[
name|itemIndex
index|]
argument_list|,
literal|0
argument_list|)
operator|==
literal|0
return|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
name|CopyOfWordDictionary
name|dic
init|=
operator|new
name|CopyOfWordDictionary
argument_list|()
decl_stmt|;
name|dic
operator|.
name|load
argument_list|(
literal|"D:/analysis-data"
argument_list|)
expr_stmt|;
name|Utility
operator|.
name|getCharType
argument_list|(
literal|'ã'
argument_list|)
expr_stmt|;
name|Utility
operator|.
name|getCharType
argument_list|(
literal|'æ±'
argument_list|)
expr_stmt|;
name|Utility
operator|.
name|getCharType
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// 0020
name|Utility
operator|.
name|getCharType
argument_list|(
literal|'ã'
argument_list|)
expr_stmt|;
comment|// 3000
name|Utility
operator|.
name|getCharType
argument_list|(
literal|'î'
argument_list|)
expr_stmt|;
comment|// E095
name|Utility
operator|.
name|getCharType
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// 3000
name|Utility
operator|.
name|getCharType
argument_list|(
literal|'\r'
argument_list|)
expr_stmt|;
comment|// 000D
name|Utility
operator|.
name|getCharType
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
comment|// 000A
name|Utility
operator|.
name|getCharType
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
comment|// 0009
block|}
block|}
end_class

end_unit


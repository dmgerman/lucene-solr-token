begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|Metaphone
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|Caverphone2
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
name|MockTokenizer
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
name|TokenStream
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
name|Tokenizer
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TestPhoneticFilterFactory
specifier|public
class|class
name|TestPhoneticFilterFactory
extends|extends
name|BaseTokenTestCase
block|{
DECL|field|REPEATS
specifier|private
specifier|static
specifier|final
name|int
name|REPEATS
init|=
literal|100000
decl_stmt|;
comment|/**    * Case: default    */
DECL|method|testFactory
specifier|public
name|void
name|testFactory
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|PhoneticFilterFactory
name|ff
init|=
operator|new
name|PhoneticFilterFactory
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"Metaphone"
argument_list|)
expr_stmt|;
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ff
operator|.
name|getEncoder
argument_list|()
operator|instanceof
name|Metaphone
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ff
operator|.
name|inject
argument_list|)
expr_stmt|;
comment|// default
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|INJECT
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ff
operator|.
name|inject
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|MAX_CODE_LENGTH
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|Metaphone
operator|)
name|ff
operator|.
name|getEncoder
argument_list|()
operator|)
operator|.
name|getMaxCodeLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Case: Failures and Exceptions    */
DECL|method|testFactoryCaseFailure
specifier|public
name|void
name|testFactoryCaseFailure
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|PhoneticFilterFactory
name|ff
init|=
operator|new
name|PhoneticFilterFactory
argument_list|()
decl_stmt|;
try|try
block|{
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"missing encoder parameter"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"XXX"
argument_list|)
expr_stmt|;
try|try
block|{
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unknown encoder parameter"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"org.apache.commons.codec.language.NonExistence"
argument_list|)
expr_stmt|;
try|try
block|{
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unknown encoder parameter"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
comment|/**    * Case: Reflection    */
DECL|method|testFactoryCaseReflection
specifier|public
name|void
name|testFactoryCaseReflection
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|PhoneticFilterFactory
name|ff
init|=
operator|new
name|PhoneticFilterFactory
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"org.apache.commons.codec.language.Metaphone"
argument_list|)
expr_stmt|;
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ff
operator|.
name|getEncoder
argument_list|()
operator|instanceof
name|Metaphone
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ff
operator|.
name|inject
argument_list|)
expr_stmt|;
comment|// default
comment|// we use "Caverphone2" as it is registered in the REGISTRY as Caverphone,
comment|// so this effectively tests reflection without package name
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"Caverphone2"
argument_list|)
expr_stmt|;
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ff
operator|.
name|getEncoder
argument_list|()
operator|instanceof
name|Caverphone2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ff
operator|.
name|inject
argument_list|)
expr_stmt|;
comment|// default
comment|// cross check with registry
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"Caverphone"
argument_list|)
expr_stmt|;
name|ff
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ff
operator|.
name|getEncoder
argument_list|()
operator|instanceof
name|Caverphone2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ff
operator|.
name|inject
argument_list|)
expr_stmt|;
comment|// default
block|}
DECL|method|testAlgorithms
specifier|public
name|void
name|testAlgorithms
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAlgorithm
argument_list|(
literal|"Metaphone"
argument_list|,
literal|"true"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"aaa"
block|,
literal|"B"
block|,
literal|"bbb"
block|,
literal|"KKK"
block|,
literal|"ccc"
block|,
literal|"ESKS"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Metaphone"
argument_list|,
literal|"false"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"KKK"
block|,
literal|"ESKS"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"DoubleMetaphone"
argument_list|,
literal|"true"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"aaa"
block|,
literal|"PP"
block|,
literal|"bbb"
block|,
literal|"KK"
block|,
literal|"ccc"
block|,
literal|"ASKS"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"DoubleMetaphone"
argument_list|,
literal|"false"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"PP"
block|,
literal|"KK"
block|,
literal|"ASKS"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Soundex"
argument_list|,
literal|"true"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A000"
block|,
literal|"aaa"
block|,
literal|"B000"
block|,
literal|"bbb"
block|,
literal|"C000"
block|,
literal|"ccc"
block|,
literal|"E220"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Soundex"
argument_list|,
literal|"false"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A000"
block|,
literal|"B000"
block|,
literal|"C000"
block|,
literal|"E220"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"RefinedSoundex"
argument_list|,
literal|"true"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A0"
block|,
literal|"aaa"
block|,
literal|"B1"
block|,
literal|"bbb"
block|,
literal|"C3"
block|,
literal|"ccc"
block|,
literal|"E034034"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"RefinedSoundex"
argument_list|,
literal|"false"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A0"
block|,
literal|"B1"
block|,
literal|"C3"
block|,
literal|"E034034"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Caverphone"
argument_list|,
literal|"true"
argument_list|,
literal|"Darda Karleen Datha Carlene"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TTA1111111"
block|,
literal|"Darda"
block|,
literal|"KLN1111111"
block|,
literal|"Karleen"
block|,
literal|"TTA1111111"
block|,
literal|"Datha"
block|,
literal|"KLN1111111"
block|,
literal|"Carlene"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Caverphone"
argument_list|,
literal|"false"
argument_list|,
literal|"Darda Karleen Datha Carlene"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TTA1111111"
block|,
literal|"KLN1111111"
block|,
literal|"TTA1111111"
block|,
literal|"KLN1111111"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"ColognePhonetic"
argument_list|,
literal|"true"
argument_list|,
literal|"Meier Schmitt Meir Schmidt"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"67"
block|,
literal|"Meier"
block|,
literal|"862"
block|,
literal|"Schmitt"
block|,
literal|"67"
block|,
literal|"Meir"
block|,
literal|"862"
block|,
literal|"Schmidt"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"ColognePhonetic"
argument_list|,
literal|"false"
argument_list|,
literal|"Meier Schmitt Meir Schmidt"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"67"
block|,
literal|"862"
block|,
literal|"67"
block|,
literal|"862"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAlgorithm
specifier|static
name|void
name|assertAlgorithm
parameter_list|(
name|String
name|algName
parameter_list|,
name|String
name|inject
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"encoder"
argument_list|,
name|algName
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"inject"
argument_list|,
name|inject
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpeed
specifier|public
name|void
name|testSpeed
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSpeedEncoding
argument_list|(
literal|"Metaphone"
argument_list|,
literal|"easgasg"
argument_list|,
literal|"ESKS"
argument_list|)
expr_stmt|;
name|checkSpeedEncoding
argument_list|(
literal|"DoubleMetaphone"
argument_list|,
literal|"easgasg"
argument_list|,
literal|"ASKS"
argument_list|)
expr_stmt|;
name|checkSpeedEncoding
argument_list|(
literal|"Soundex"
argument_list|,
literal|"easgasg"
argument_list|,
literal|"E220"
argument_list|)
expr_stmt|;
name|checkSpeedEncoding
argument_list|(
literal|"RefinedSoundex"
argument_list|,
literal|"easgasg"
argument_list|,
literal|"E034034"
argument_list|)
expr_stmt|;
name|checkSpeedEncoding
argument_list|(
literal|"Caverphone"
argument_list|,
literal|"Carlene"
argument_list|,
literal|"KLN1111111"
argument_list|)
expr_stmt|;
name|checkSpeedEncoding
argument_list|(
literal|"ColognePhonetic"
argument_list|,
literal|"Schmitt"
argument_list|,
literal|"862"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkSpeedEncoding
specifier|private
name|void
name|checkSpeedEncoding
parameter_list|(
name|String
name|encoder
parameter_list|,
name|String
name|toBeEncoded
parameter_list|,
name|String
name|estimated
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
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
name|REPEATS
condition|;
name|i
operator|++
control|)
block|{
name|assertAlgorithm
argument_list|(
name|encoder
argument_list|,
literal|"false"
argument_list|,
name|toBeEncoded
argument_list|,
operator|new
name|String
index|[]
block|{
name|estimated
block|}
argument_list|)
expr_stmt|;
block|}
name|long
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|encoder
operator|+
literal|" encodings per msec: "
operator|+
operator|(
name|REPEATS
operator|/
name|duration
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


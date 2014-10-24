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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
DECL|class|TestVersion
specifier|public
class|class
name|TestVersion
extends|extends
name|LuceneTestCase
block|{
DECL|method|testOnOrAfter
specifier|public
name|void
name|testOnOrAfter
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Field
name|field
range|:
name|Version
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|field
operator|.
name|getType
argument_list|()
operator|==
name|Version
operator|.
name|class
condition|)
block|{
name|Version
name|v
init|=
operator|(
name|Version
operator|)
name|field
operator|.
name|get
argument_list|(
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"LATEST must be always onOrAfter("
operator|+
name|v
operator|+
literal|")"
argument_list|,
name|Version
operator|.
name|LATEST
operator|.
name|onOrAfter
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|Version
operator|.
name|LUCENE_6_0_0
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|)
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"5.0.0"
argument_list|,
name|Version
operator|.
name|LUCENE_5_0_0
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"6.0.0"
argument_list|,
name|Version
operator|.
name|LUCENE_6_0_0
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseLeniently
specifier|public
name|void
name|testParseLeniently
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"5.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"5.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_50"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_5_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_5_0_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"6.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"6.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_60"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_6_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_6_0_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LATEST"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"latest"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_CURRENT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"lucene_current"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseLenientlyExceptions
specifier|public
name|void
name|testParseLenientlyExceptions
parameter_list|()
block|{
try|try
block|{
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"LUCENE"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_610"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"LUCENE_610"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE61"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"LUCENE61"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_6.0.0"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"LUCENE_6.0.0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseLenientlyOnAllConstants
specifier|public
name|void
name|testParseLenientlyOnAllConstants
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|atLeastOne
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|Version
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|field
operator|.
name|getType
argument_list|()
operator|==
name|Version
operator|.
name|class
condition|)
block|{
name|atLeastOne
operator|=
literal|true
expr_stmt|;
name|Version
name|v
init|=
operator|(
name|Version
operator|)
name|field
operator|.
name|get
argument_list|(
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|v
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
name|v
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|v
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|v
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|atLeastOne
argument_list|)
expr_stmt|;
block|}
DECL|method|testParse
specifier|public
name|void
name|testParse
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|,
name|Version
operator|.
name|parse
argument_list|(
literal|"6.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|,
name|Version
operator|.
name|parse
argument_list|(
literal|"5.0.0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Version does not pass judgement on the major version:
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Version
operator|.
name|parse
argument_list|(
literal|"1.0"
argument_list|)
operator|.
name|major
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|Version
operator|.
name|parse
argument_list|(
literal|"7.0.0"
argument_list|)
operator|.
name|major
argument_list|)
expr_stmt|;
block|}
DECL|method|testForwardsCompatibility
specifier|public
name|void
name|testForwardsCompatibility
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|Version
operator|.
name|parse
argument_list|(
literal|"5.10.20"
argument_list|)
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseExceptions
specifier|public
name|void
name|testParseExceptions
parameter_list|()
block|{
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"LUCENE_6_0_0"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"LUCENE_6_0_0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.256"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.256"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.-1"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.-1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.1.256"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.1.256"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.1.-1"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.1.-1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.1.1.3"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.1.1.3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.1.1.-1"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.1.1.-1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.1.1.1"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.1.1.1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.1.1.2"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.1.1.2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.0.0.0"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.0.0.0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6.0.0.1.42"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6.0.0.1.42"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Version
operator|.
name|parse
argument_list|(
literal|"6..0.1"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// pass
name|assertTrue
argument_list|(
name|pe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"6..0.1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDeprecations
specifier|public
name|void
name|testDeprecations
parameter_list|()
throws|throws
name|Exception
block|{
comment|// all but the latest version should be deprecated
name|boolean
name|atLeastOne
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|Version
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|field
operator|.
name|getType
argument_list|()
operator|==
name|Version
operator|.
name|class
condition|)
block|{
name|atLeastOne
operator|=
literal|true
expr_stmt|;
name|Version
name|v
init|=
operator|(
name|Version
operator|)
name|field
operator|.
name|get
argument_list|(
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|dep
init|=
name|field
operator|.
name|isAnnotationPresent
argument_list|(
name|Deprecated
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|)
operator|&&
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"LUCENE_CURRENT"
argument_list|)
operator|==
literal|false
condition|)
block|{
name|assertFalse
argument_list|(
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|" should not be deprecated"
argument_list|,
name|dep
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|" should be deprecated"
argument_list|,
name|dep
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertTrue
argument_list|(
name|atLeastOne
argument_list|)
expr_stmt|;
block|}
DECL|method|testLatestVersionCommonBuild
specifier|public
name|void
name|testLatestVersionCommonBuild
parameter_list|()
block|{
comment|// common-build.xml sets 'tests.LUCENE_VERSION', if not, we skip this test!
name|String
name|commonBuildVersion
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.LUCENE_VERSION"
argument_list|)
decl_stmt|;
name|assumeTrue
argument_list|(
literal|"Null 'tests.LUCENE_VERSION' test property. You should run the tests with the official Lucene build file"
argument_list|,
name|commonBuildVersion
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Version.LATEST does not match the one given in common-build.xml"
argument_list|,
name|Version
operator|.
name|LATEST
operator|.
name|toString
argument_list|()
argument_list|,
name|commonBuildVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqualsHashCode
specifier|public
name|void
name|testEqualsHashCode
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|String
name|version
init|=
literal|""
operator|+
operator|(
literal|4
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|1
argument_list|)
operator|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Version
name|v1
init|=
name|Version
operator|.
name|parseLeniently
argument_list|(
name|version
argument_list|)
decl_stmt|;
name|Version
name|v2
init|=
name|Version
operator|.
name|parseLeniently
argument_list|(
name|version
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|v1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|v2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
specifier|final
name|int
name|iters
init|=
literal|10
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|String
name|v
init|=
literal|""
operator|+
operator|(
literal|4
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|1
argument_list|)
operator|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|Version
operator|.
name|parseLeniently
argument_list|(
name|v
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|,
name|v1
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|parseLeniently
argument_list|(
name|v
argument_list|)
argument_list|,
name|v1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|Version
operator|.
name|parseLeniently
argument_list|(
name|v
argument_list|)
operator|.
name|equals
argument_list|(
name|v1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit


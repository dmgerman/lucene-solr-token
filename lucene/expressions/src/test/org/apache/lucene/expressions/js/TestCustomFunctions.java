begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.expressions.js
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|Method
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
name|Collections
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
name|lucene
operator|.
name|expressions
operator|.
name|Expression
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|ClassWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Opcodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|commons
operator|.
name|GeneratorAdapter
import|;
end_import

begin_comment
comment|/** Tests customing the function map */
end_comment

begin_class
DECL|class|TestCustomFunctions
specifier|public
class|class
name|TestCustomFunctions
extends|extends
name|LuceneTestCase
block|{
DECL|field|DELTA
specifier|private
specifier|static
name|double
name|DELTA
init|=
literal|0.0000001
decl_stmt|;
comment|/** empty list of methods */
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|ParseException
name|expected
init|=
name|expectThrows
argument_list|(
name|ParseException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(20)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid expression 'sqrt(20)': Unrecognized function call (sqrt)."
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getErrorOffset
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** using the default map explicitly */
DECL|method|testDefaultList
specifier|public
name|void
name|testDefaultList
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
name|JavascriptCompiler
operator|.
name|DEFAULT_FUNCTIONS
decl_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(20)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
literal|20
argument_list|)
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|zeroArgMethod
specifier|public
specifier|static
name|double
name|zeroArgMethod
parameter_list|()
block|{
return|return
literal|5
return|;
block|}
comment|/** tests a method with no arguments */
DECL|method|testNoArgMethod
specifier|public
name|void
name|testNoArgMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"zeroArgMethod"
argument_list|)
argument_list|)
expr_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|oneArgMethod
specifier|public
specifier|static
name|double
name|oneArgMethod
parameter_list|(
name|double
name|arg1
parameter_list|)
block|{
return|return
literal|3
operator|+
name|arg1
return|;
block|}
comment|/** tests a method with one arguments */
DECL|method|testOneArgMethod
specifier|public
name|void
name|testOneArgMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"oneArgMethod"
argument_list|,
name|double
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo(3)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|threeArgMethod
specifier|public
specifier|static
name|double
name|threeArgMethod
parameter_list|(
name|double
name|arg1
parameter_list|,
name|double
name|arg2
parameter_list|,
name|double
name|arg3
parameter_list|)
block|{
return|return
name|arg1
operator|+
name|arg2
operator|+
name|arg3
return|;
block|}
comment|/** tests a method with three arguments */
DECL|method|testThreeArgMethod
specifier|public
name|void
name|testThreeArgMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"threeArgMethod"
argument_list|,
name|double
operator|.
name|class
argument_list|,
name|double
operator|.
name|class
argument_list|,
name|double
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo(3, 4, 5)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
comment|/** tests a map with 2 functions */
DECL|method|testTwoMethods
specifier|public
name|void
name|testTwoMethods
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"zeroArgMethod"
argument_list|)
argument_list|)
expr_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"oneArgMethod"
argument_list|,
name|double
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo() + bar(3)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
comment|/** tests invalid methods that are not allowed to become variables to be mapped */
DECL|method|testInvalidVariableMethods
specifier|public
name|void
name|testInvalidVariableMethods
parameter_list|()
block|{
name|ParseException
name|expected
init|=
name|expectThrows
argument_list|(
name|ParseException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"method()"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid expression 'method()': Unrecognized function call (method)."
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|expected
operator|.
name|getErrorOffset
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|ParseException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"method.method(1)"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid expression 'method.method(1)': Unrecognized function call (method.method)."
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|expected
operator|.
name|getErrorOffset
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|ParseException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"1 + method()"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid expression '1 + method()': Unrecognized function call (method)."
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|expected
operator|.
name|getErrorOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|bogusReturnType
specifier|public
specifier|static
name|String
name|bogusReturnType
parameter_list|()
block|{
return|return
literal|"bogus!"
return|;
block|}
comment|/** wrong return type: must be double */
DECL|method|testWrongReturnType
specifier|public
name|void
name|testWrongReturnType
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"bogusReturnType"
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"does not return a double"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|bogusParameterType
specifier|public
specifier|static
name|double
name|bogusParameterType
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
comment|/** wrong param type: must be doubles */
DECL|method|testWrongParameterType
specifier|public
name|void
name|testWrongParameterType
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"bogusParameterType"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo(2)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"must take only double parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|nonStaticMethod
specifier|public
name|double
name|nonStaticMethod
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** wrong modifiers: must be static */
DECL|method|testWrongNotStatic
specifier|public
name|void
name|testWrongNotStatic
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"nonStaticMethod"
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is not static"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|nonPublicMethod
specifier|static
name|double
name|nonPublicMethod
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** wrong modifiers: must be public */
DECL|method|testWrongNotPublic
specifier|public
name|void
name|testWrongNotPublic
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"nonPublicMethod"
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not public"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|NestedNotPublic
specifier|static
class|class
name|NestedNotPublic
block|{
DECL|method|method
specifier|public
specifier|static
name|double
name|method
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** wrong class modifiers: class containing method is not public */
DECL|method|testWrongNestedNotPublic
specifier|public
name|void
name|testWrongNestedNotPublic
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|NestedNotPublic
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"method"
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not public"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Classloader that can be used to create a fake static class that has one method returning a static var */
DECL|class|Loader
specifier|static
specifier|final
class|class
name|Loader
extends|extends
name|ClassLoader
implements|implements
name|Opcodes
block|{
DECL|method|Loader
name|Loader
parameter_list|(
name|ClassLoader
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
DECL|method|createFakeClass
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|createFakeClass
parameter_list|()
block|{
name|String
name|className
init|=
name|TestCustomFunctions
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"$Foo"
decl_stmt|;
name|ClassWriter
name|classWriter
init|=
operator|new
name|ClassWriter
argument_list|(
name|ClassWriter
operator|.
name|COMPUTE_FRAMES
operator||
name|ClassWriter
operator|.
name|COMPUTE_MAXS
argument_list|)
decl_stmt|;
name|classWriter
operator|.
name|visit
argument_list|(
name|Opcodes
operator|.
name|V1_5
argument_list|,
name|ACC_PUBLIC
operator||
name|ACC_SUPER
operator||
name|ACC_FINAL
operator||
name|ACC_SYNTHETIC
argument_list|,
name|className
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Type
operator|.
name|getInternalName
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|commons
operator|.
name|Method
name|m
init|=
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|commons
operator|.
name|Method
operator|.
name|getMethod
argument_list|(
literal|"void<init>()"
argument_list|)
decl_stmt|;
name|GeneratorAdapter
name|constructor
init|=
operator|new
name|GeneratorAdapter
argument_list|(
name|ACC_PRIVATE
operator||
name|ACC_SYNTHETIC
argument_list|,
name|m
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|classWriter
argument_list|)
decl_stmt|;
name|constructor
operator|.
name|loadThis
argument_list|()
expr_stmt|;
name|constructor
operator|.
name|loadArgs
argument_list|()
expr_stmt|;
name|constructor
operator|.
name|invokeConstructor
argument_list|(
name|Type
operator|.
name|getType
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|constructor
operator|.
name|returnValue
argument_list|()
expr_stmt|;
name|constructor
operator|.
name|endMethod
argument_list|()
expr_stmt|;
name|GeneratorAdapter
name|gen
init|=
operator|new
name|GeneratorAdapter
argument_list|(
name|ACC_STATIC
operator||
name|ACC_PUBLIC
operator||
name|ACC_SYNTHETIC
argument_list|,
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|commons
operator|.
name|Method
operator|.
name|getMethod
argument_list|(
literal|"double bar()"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|classWriter
argument_list|)
decl_stmt|;
name|gen
operator|.
name|push
argument_list|(
literal|2.0
argument_list|)
expr_stmt|;
name|gen
operator|.
name|returnValue
argument_list|()
expr_stmt|;
name|gen
operator|.
name|endMethod
argument_list|()
expr_stmt|;
name|byte
index|[]
name|bc
init|=
name|classWriter
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
name|defineClass
argument_list|(
name|className
argument_list|,
name|bc
argument_list|,
literal|0
argument_list|,
name|bc
operator|.
name|length
argument_list|)
return|;
block|}
block|}
comment|/** uses this test with a different classloader and tries to    * register it using the default classloader, which should fail */
DECL|method|testClassLoader
specifier|public
name|void
name|testClassLoader
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassLoader
name|thisLoader
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|Loader
name|childLoader
init|=
operator|new
name|Loader
argument_list|(
name|thisLoader
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|fooClass
init|=
name|childLoader
operator|.
name|createFakeClass
argument_list|()
decl_stmt|;
name|Method
name|barMethod
init|=
name|fooClass
operator|.
name|getMethod
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"bar"
argument_list|,
name|barMethod
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|thisLoader
argument_list|,
name|fooClass
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|thisLoader
argument_list|,
name|barMethod
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
comment|// this should pass:
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"bar()"
argument_list|,
name|functions
argument_list|,
name|childLoader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
comment|// use our classloader, not the foreign one, which should fail!
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"bar()"
argument_list|,
name|functions
argument_list|,
name|thisLoader
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is not declared by a class which is accessible by the given parent ClassLoader"
argument_list|)
argument_list|)
expr_stmt|;
comment|// mix foreign and default functions
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|mixedFunctions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|JavascriptCompiler
operator|.
name|DEFAULT_FUNCTIONS
argument_list|)
decl_stmt|;
name|mixedFunctions
operator|.
name|putAll
argument_list|(
name|functions
argument_list|)
expr_stmt|;
name|expr
operator|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"bar()"
argument_list|,
name|mixedFunctions
argument_list|,
name|childLoader
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
name|expr
operator|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(20)"
argument_list|,
name|mixedFunctions
argument_list|,
name|childLoader
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
literal|20
argument_list|)
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
comment|// use our classloader, not the foreign one, which should fail!
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"bar()"
argument_list|,
name|mixedFunctions
argument_list|,
name|thisLoader
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is not declared by a class which is accessible by the given parent ClassLoader"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|MESSAGE
specifier|static
name|String
name|MESSAGE
init|=
literal|"This should not happen but it happens"
decl_stmt|;
DECL|class|StaticThrowingException
specifier|public
specifier|static
class|class
name|StaticThrowingException
block|{
DECL|method|method
specifier|public
specifier|static
name|double
name|method
parameter_list|()
block|{
throw|throw
operator|new
name|ArithmeticException
argument_list|(
name|MESSAGE
argument_list|)
throw|;
block|}
block|}
comment|/** the method throws an exception. We should check the stack trace that it contains the source code of the expression as file name. */
DECL|method|testThrowingException
specifier|public
name|void
name|testThrowingException
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|StaticThrowingException
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"method"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"3 * foo() / 5"
decl_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
name|source
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|ArithmeticException
name|expected
init|=
name|expectThrows
argument_list|(
name|ArithmeticException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|expected
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"JavascriptCompiler$CompiledExpression.evaluate("
operator|+
name|source
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** test that namespaces work with custom expressions. */
DECL|method|testNamespaces
specifier|public
name|void
name|testNamespaces
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo.bar"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"zeroArgMethod"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"foo.bar()"
decl_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
name|source
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


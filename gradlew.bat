@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
setlocal

set GRADLE_OPTS="-Dorg.gradle.daemon=false"

if not "%GRADLE_HOME%"=="" (
    set JAVA_HOME=%GRADLE_HOME%\jdk
)

if "%JAVA_HOME%"=="" (
    set JAVA_HOME=%JAVA_HOME%
)

if not "%JAVA_HOME%"=="" (
    set _JAVACMD="%JAVA_HOME%\bin\java.exe"
) else (
    set _JAVACMD=java
)

set _CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

"%_JAVACMD%" %GRADLE_OPTS% -classpath "%_CLASSPATH%" -Dgradle.home="%APP_HOME%" org.gradle.wrapper.GradleWrapperMain %*

endlocal

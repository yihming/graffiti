# Require at least version 2.8 of CMake
cmake_minimum_required(VERSION 2.8)

set(PROJ_NAME thermal_flux_example)

# The project name. A project can have multiple targets
project(${PROJ_NAME})

# Set up C++0x
if(APPLE)
  set(CMAKE_XCODE_ATTRIBUTE_GCC_VERSION "com.apple.compilers.llvm.clang.1_0")
  set(CMAKE_XCODE_ATTRIBUTE_CLANG_CXX_LANGUAGE_STANDARD "c++0x")
  set(CMAKE_XCODE_ATTRIBUTE_CLANG_CXX_LIBRARY "libc++")
  set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS} -std=c++0x -stdlib=libc++ -g -Wall")
endif(APPLE)

include_directories("/opt/local/include")

# Add the executable target. It does not need to 
# have the same name as the project
add_executable(${PROJ_NAME}     # Name of the program
  example.cpp
)

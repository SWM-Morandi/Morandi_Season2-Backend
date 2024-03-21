package kr.co.morandi.backend.domain.exammanagement.tempcode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    JAVA("""
        public class Main {
            public static void main(String[] args) {
                System.out.println("Hello World\n");
            }
        }
        """),
     CPP("""
        #include <iostream>
        using namespace std;
        
        int main() {
            cout << "Hello World" << endl;
            return 0;
        }
        """),
    PYTHON("""
            print("Hello World")
        """);

    private final String initialCode;
}

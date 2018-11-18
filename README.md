# NCSA Toolbox
This is a toolbox app currently focused towards members of NCSA (Networking and Computing Student Association) at MTU (Michigan Technological University). Although that is the case, anyone is free to build the APK themselves and mess around with them. I'm a new developer to Android so if you notice something that is particularly bad or you have suggestions go ahead and make a pull request or whatnot! If you want to learn more about NSCA go ahead visit our website over <a href="https://www.ncsa.tech">here!</a>

## Current Features 
- Toolbox:
  - Subnetter
  Allows one to subnet an arbitrary network with an arbitrary amount of hosts and such.
- Knowledge Base:
  - Presentations
  - PDFS
  - Videos (Partially)

## To-Do:
- Fixed how videos show up
- Add things to the about
- Have settings do something
- Hook up social media links

## How to Build the APK
### Prerequisites 
#### Java
- I recommend <a href="https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html">Java 8</a>
- Apparently JDK 9 may have problems with Gradle, use at your own risk!
#### Source
- GIT: Clone with `git clone https://github.com/MagneticZer0/NCSA-Toolbox.git`
- What's GIT?: Don't worry, you can download it through <a href="https://github.com/MagneticZer0/NCSA-Toolbox/archive/master.zip">here!</a>
#### Android Studio (Optional)
- You can download Android Studio <a href="https://developer.android.com/studio/">here</a>
#### Steps:
1. Open console/bash/terminal
2. Run `cd NCSA-Toolbox` or `cd (directory)` if downloaded directory name is different.
3. After run `gradlew assembleDebug` or if you want to install directly to your device, plug it in and run `gradlew installDebug`

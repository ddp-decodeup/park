import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_appbar.dart';
import 'package:park_enfoecement/app/shared/widgets/drawer.dart';

class AppScaffold extends StatelessWidget {
  final String userName;
  final Widget? body;
  final Widget? floatingActionButton;
  final VoidCallback? onBack;
  final Widget? bottomNavigationBar;
  final bool showDivider;
  final bool showShadowWhileScroll;

  const AppScaffold({
    super.key,
    this.body,
    this.floatingActionButton,
    this.userName = "",
    this.onBack,
    this.bottomNavigationBar,
    this.showDivider = false,
    this.showShadowWhileScroll = false,
  });

  @override
  Widget build(BuildContext context) {
    GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();

    return Scaffold(
      key: _scaffoldKey,

      endDrawer: CustomDrawer(),
      appBar: CustomAppBar(
        showShadowWhileScroll:showShadowWhileScroll ,
        title: userName,
        showDivider: showDivider,
        onTapHamburger: () {
          _scaffoldKey.currentState?.openEndDrawer();
        },
        onBack: onBack,
      ),
      body: body,
      floatingActionButton: floatingActionButton,
      bottomNavigationBar: bottomNavigationBar,
    );
  }
}

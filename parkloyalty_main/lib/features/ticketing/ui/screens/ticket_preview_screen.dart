import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/shared/utils/extensions/context_extensions.dart';
import 'package:park_enfoecement/features/ticketing/controller/ticket_preview_controller.dart';
import 'package:park_enfoecement/features/ticketing/ui/screens/ticket_post_page.dart';
import 'package:qr_flutter/qr_flutter.dart';

import '../../data/models/ticket_creation_response.dart';

class TicketPreviewScreen extends StatelessWidget {
  const TicketPreviewScreen({super.key});


  @override
  Widget build(BuildContext context) {
    final controller = Get.find<TicketPreviewController>();

    return GetBuilder<TicketPreviewController>(
      id: 'key',
        builder: (controller) {
          return TicketWidget();
        });
  }
}

class TicketWidget extends StatelessWidget {
  const TicketWidget({super.key});

  @override
  Widget build(BuildContext context) {
    // or build dynamically

    return GetBuilder<TicketPreviewController>(
      builder: (controller) {
        final header = controller.model.headerDetails;
        final officer = controller.model.officerDetails;
        final location = controller.model.locationDetails;
        final vehicle = controller.model.vehicleDetails;
        final violation = controller.model.violationDetails;

        final locationText = "${location?.lot ?? ''} ${location?.street ?? ''}".trim();

        final paymentUrl = "https://lazkc.com/";
        return RepaintBoundary(
          key: controller.previewKey,
          child: SingleChildScrollView(
            child: Container(
              width: 380,
              color: const Color(0xFFEDEDED),
              padding: EdgeInsets.only(left: 16, right: 16, top: context.padding.top),
              child: DefaultTextStyle(
                style: const TextStyle(fontFamily: 'Courier', fontSize: 16, letterSpacing: -1, color: Colors.black),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    /// HEADER
                    const Center(
                      child: Text(
                        "LAZ PARKING",
                        style: TextStyle(fontSize: 26, fontWeight: FontWeight.bold, letterSpacing: 2),
                      ),
                    ),
                    const SizedBox(height: 6),
                    const Center(child: Text("KANSAS CITY PRIVATE LOTS", style: TextStyle(letterSpacing: -2))),
                    const SizedBox(height: 10),

                    sectionTitle("PARKING NOTICE"),

                    _box([
                      _tripleRow(
                        "Citation Number",
                        header?.citationNumber ?? '',
                        "Date",
                        _formatDate(header?.timestamp),
                        "Time",
                        _formatTime(header?.timestamp),
                      ),
                      const SizedBox(height: 12),
                      _doubleRow("Officer ID", officer?.badgeId ?? '', "Agency", officer?.agency ?? ''),
                      const SizedBox(height: 12),
                      const Text("Location"),
                      Text(locationText),
                    ]),

                    sectionTitle("VIOLATION"),

                    _box([
                      _doubleRow(
                        "Code",
                        violation?.code ?? '',
                        "Fine",
                        "\$ ${(violation?.fine ?? 0).toStringAsFixed(2)}",
                      ),
                      const SizedBox(height: 16),
                      Text(violation?.description ?? ''),
                    ]),

                    sectionTitle("VEHICLE INFORMATION"),

                    _box([
                      _doubleRow("License Number", vehicle?.lpNumber ?? '', "State", vehicle?.state ?? ''),
                      const SizedBox(height: 12),
                      _doubleRow("Make", vehicle?.make ?? '', "Color", vehicle?.color ?? ''),
                      const SizedBox(height: 12),
                      _doubleRow("Vin Number", vehicle?.vinNumber ?? '', "Model", vehicle?.model ?? ''),
                    ]),

                    const SizedBox(height: 60),

                    const Center(child: Text("SCAN TO PAY", style: TextStyle(fontSize: 20))),
                    const SizedBox(height: 5),

                    Center(
                      child: GestureDetector(
                        onTap: () => controller.captureWidgetToJpg(controller.previewKey),
                        child: QrImageView(data: paymentUrl, size: 60),
                      ),
                    ),

                    const SizedBox(height: 5),

                    Center(child: Text(paymentUrl, style: const TextStyle(fontSize: 18))),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  Text sectionTitle(String title) {
    return Text(title, style: TextStyle(fontSize: 20, letterSpacing: -1));
  }

  Widget _box(List<Widget> children) {
    return Container(
      padding: const EdgeInsets.all(10),
      decoration: BoxDecoration(border: Border.all(color: Colors.black, width: 1)),
      child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: children),
    );
  }

  Widget _doubleRow(String l1, String v1, String l2, String v2) {
    return Row(
      children: [
        Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(l1), Text(v1)]),
        ),
        Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(l2), Text(v2)]),
        ),
      ],
    );
  }

  Widget _tripleRow(String l1, String v1, String l2, String v2, String l3, String v3) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(l1), Text(v1)]),
        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(l2), Text(v2)]),
        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(l3), Text(v3)]),
      ],
    );
  }

  String _formatDate(String? timestamp) {
    if (timestamp == null) return '';
    try {
      final dt = DateTime.parse(timestamp);
      return "${dt.month}/${dt.day}/${dt.year}";
    } catch (_) {
      return '';
    }
  }

  String _formatTime(String? timestamp) {
    if (timestamp == null) return '';
    try {
      final dt = DateTime.parse(timestamp);
      return "${dt.hour}:${dt.minute.toString().padLeft(2, '0')}";
    } catch (_) {
      return '';
    }
  }
}

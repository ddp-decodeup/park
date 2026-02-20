import 'package:intl/intl.dart';

abstract class DateUtil {
  static String simplifiedDate(DateTime d) {
    return DateFormat('dd-MM-yyyy hh:mm a').format(d);
  }

  static String citationHistoryDateFormat(DateTime d) {
    return DateFormat('dd-MM-yyyy - hh:mm a').format(d);
  }

  static String simplifiedDate2(DateTime d) {
    return DateFormat('dd MMM, yyyy - hh:mm a').format(d);
  }

  static String mmDDYYYY(DateTime d) {
    return DateFormat('MM/dd/yyyy').format(d);
  }

  static String getHearingDate() {
    final hearingDate = DateTime.now().add(const Duration(days: 25));

    final formattedDate =
        "${hearingDate.month.toString().padLeft(2, '0')}/"
        "${hearingDate.day.toString().padLeft(2, '0')}/"
        "${hearingDate.year}";

    return "$formattedDate 9AM-2:30PM";
  }


  static String ddMMMMyyyy(DateTime d) {
    return DateFormat('dd MMMM, yyyy').format(d);
  }

  static String hhmm(DateTime d) {
    return DateFormat('hh:mm a').format(d);
  }

  static String getMMddhhmm(DateTime d) {
    return DateFormat('MM-dd hh:mm a').format(d);
  }

  static String timeDifferenceHHMMSS(DateTime given) {
    final now = DateTime.now().toLocal();
    final diff = now.difference(given);
    // final diff = given.to.difference(given.toLocal());

    final hours = diff.inHours.toString().padLeft(2, '0');
    final minutes = (diff.inMinutes % 60).toString().padLeft(2, '0');
    final seconds = (diff.inSeconds % 60).toString().padLeft(2, '0');

    return '$hours:$minutes:$seconds';
  }

  static String minutesToHoursMinutes(int totalMinutes) {
    final hours = totalMinutes ~/ 60;
    final minutes = totalMinutes % 60;

    return '$hours hours $minutes minutes';
  }

  static String deductTimeFromMinutes({
    required int totalMinutes,
    required String timeToDeduct, // HH:mm:ss
  }) {
    // Convert total minutes → seconds
    int totalSeconds = totalMinutes * 60;

    // Parse HH:mm:ss
    final parts = timeToDeduct.split(':').map(int.parse).toList();
    final deductSeconds = parts[0] * 3600 + parts[1] * 60 + parts[2];

    // Deduct & clamp to zero
    final remainingSeconds = (totalSeconds - deductSeconds).clamp(0, totalSeconds);

    // Convert back to HH:mm:ss
    final hours = remainingSeconds ~/ 3600;
    final minutes = (remainingSeconds % 3600) ~/ 60;
    final seconds = remainingSeconds % 60;

    String twoDigits(int n) => n.toString().padLeft(2, '0');

    return '${twoDigits(hours)}:${twoDigits(minutes)}:${twoDigits(seconds)}';
  }

  static String timeDiffOrZero(DateTime parkTime, int totalMinutes) {
    final now = DateTime.now();
    final diff = now.difference(parkTime).abs();

    if (diff.inMinutes > totalMinutes) {
      return '00:00:00';
    }

    final hours = diff.inHours.toString().padLeft(2, '0');
    final minutes = (diff.inMinutes % 60).toString().padLeft(2, '0');
    final seconds = (diff.inSeconds % 60).toString().padLeft(2, '0');

    return '$hours:$minutes:$seconds';
  }

  static String getReqDate(DateTime d) {
    String s = d.toUtc().toIso8601String();
    final datePart = s.split('T').first;
    return '${datePart}T03:00:00Z';
  }

  static String ddMMyyyy(DateTime d) {
    return DateFormat('dd/MM/yyyy').format(d);
  }
}

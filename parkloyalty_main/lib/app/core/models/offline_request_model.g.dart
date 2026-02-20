// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'offline_request_model.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class OfflineRequestAdapter extends TypeAdapter<OfflineRequest> {
  @override
  final int typeId = 1;

  @override
  OfflineRequest read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return OfflineRequest(
      url: fields[0] as String,
      method: fields[1] as String,
      body: (fields[2] as Map).cast<String, dynamic>(),
      createdAt: fields[3].toString(),
    );
  }

  @override
  void write(BinaryWriter writer, OfflineRequest obj) {
    writer
      ..writeByte(4)
      ..writeByte(0)
      ..write(obj.url)
      ..writeByte(1)
      ..write(obj.method)
      ..writeByte(2)
      ..write(obj.body)
      ..writeByte(3)
      ..write(obj.createdAt);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is OfflineRequestAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}
